Spring Data REST, when combined with JPA, is a tool allowing for some pretty awesome productivity in building web services backed by a relational database. Just write some annotated entity classes and a bare repository subinterface, and you're off! - full CRUD functionality on your database schema exposed as JSON endpoints.

What is less widely discussed, however, is how to write a client capable of actually invoking these endpoints. The Spring HATEOAS project offers a useful first step. But the great strength of JPA (or, some would argue, its great weakness...) is its ability to lazily retrieve an entity's associations, without requiring any knowledge of the structure of the underlying database schema. How can this paradigm be extended to the resource-based interactions that comprise a REST conversation?

In this article, I briefly give an overview of Spring Data REST, and describe a simple (but production-realistic) web service built using it. I then explain how this can be accessed using a web client built on existing Spring libraries, and outline some of the pitfalls and limitations you may encounter when attempting this. Finally, I introduce Black Pepper's HAL Client, a library we've developed to greatly simplify Spring Data REST web client code by supporting, among other things, transparent traversal into associated resources.

## Introduction to Spring Data REST ##

Spring Data REST provides a Spring MVC controller that delegates to Spring Data repositories via the HTTP verbs and URIs you would expect of a RESTful interface, for example:

* GET /customers - return all customers
* GET /customers/1 - return the customer with database identifier "1"
* POST /customers - persist a new customer

It produces and consumes the JSON+HAL content type, as described by the HAL standard. HAL defines a means of embedding links to associated resources within a JSON payload in a really very trendy HATEOASy way. This is important because discussion of web clients in this article is therefore not specific to Spring Data REST, and is in fact applicable to any JSON+HAL service.

When Spring Data REST marshals an entity to JSON+HAL, "simple" properties (strings, numbers etc.) will be added to the payload as JSON properties in the manner you would expect from the underlying Object-JSON mapping framework, Jackson. But for "association" properties:

* if they're of a type **with** a repository, a HAL link to the associated resource will be added to the payload - we call this a *linked association*.
* if they're of a type **without** a repository, the associated resource will be inlined into the payload - we call this an *inline association*.

I make this distinction now because it's so fundamental to how a mapping HAL client must behave - it will become clearer when I show an example JSON+HAL response shortly.

## Our Banking System ##

In time-honoured tradition, I'll use a banking system of customers and accounts as the example for our application. In our database we have account types and customers as aggregate roots. A customer can have one or more accounts, each of which comprises an account type and a credit limit.

We have a single customer in our database, "Jeremy Corbyn", who has an account of type "Aardvantage" with credit limit £2000, and a "PlatyPlus" account with limit £1000. So our database schema looks as follows:

Our model:

```
@Entity
public class AccountType {

	@Id private int id;
	private String name;

  // getters go here
}
```

```
@Entity
public class Customer {

	@Id private int id;
	private String name;
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
	private Set<Account> accounts = new LinkedHashSet<>();

  // getters go here
}
```

```
@Entity
public class Account {

	@Id private int id;
	@ManyToOne private Customer customer;
	@ManyToOne private AccountType type;
	private BigDecimal creditLimit;

  // getters go here
}
```

And our two repositories:

```
public interface AccountTypeRepository extends CrudRepository<AccountType, Integer> {}
```

```
public interface CustomerRepository extends CrudRepository<Customer, Integer> {}
```

Then all that is required is to access http://localhost:8080/customers/1 to retrieve the JSON+HAL representation of that customer. At long last, as promised, this looks like the following:

```
{
  "name" : "Jeremy Corbyn",
  "accounts" : [ {
    "creditLimit" : 1000.00,
    "_links" : {
      "customer" : {
        "href" : "http://localhost:8080/customers/1"
      },
      "type" : {
        "href" : "http://localhost:8080/accountTypes/2"
      }
    }
  }, {
    "creditLimit" : 2000.00,
    "_links" : {
      "customer" : {
        "href" : "http://localhost:8080/customers/1"
      },
      "type" : {
        "href" : "http://localhost:8080/accountTypes/1"
      }
    }
  } ],
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/customers/1"
    },
    "customer" : {
      "href" : "http://localhost:8080/customers/1"
    }
  }
}
```

## Retrieving a Customer's Accounts ##

So, let's say we're conducting an internal credit check on Jeremy Corbyn and we would like to know the types of accounts he holds, together with these accounts' credit limits.

We will use Spring's RestTemplate to query the service. As we know the service returns a JSON+HAL response, we will configure our RestTemplate with the Jackson2HalModule from the Spring HATEOAS project. This in turn will allow us to use the Spring HATEOAS Resource class in our client-side model so we can be aware of any HAL links in the response.

Creating our RestTemplate is pretty straightforward:

```
ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
    .modules(new Jackson2HalModule())
    .build();

RestTemplate restTemplate = new RestTemplate(Arrays.asList(
    new MappingJackson2HttpMessageConverter(objectMapper)));
```

### Creating the Client Model ###

We create a model specifically for use in our client - this of course is vital to prevent microservice coupling hell. The objects returned from our RestTemplate invocations will be of these types.

```
public class AccountType {
	private String name;

  // getter & setter go here
}
```

```
public class Customer {
	private String name;
	private List<Resource<Account>> accounts;

  // getters & setters go here
}
```

```
public class Account {
	private BigDecimal creditLimit;

  // getter & setter go here
```

There's a couple of things to notice here:
* Customer.accounts must be declared as a List<Resource<Account>> - this is an inline association; if it were declared as a List<Account> there would be no way to retrieve the HAL links for each account; and
* There is no Account.type - this is a linked association; the type can only be retrieved by following the HAL link for each entry in Customer.accounts.

### Retrieving the Account Details ###

So now we can invoke methods on our RestTemplate in order to get all the information we need:

```
Customer customer = restTemplate.getForObject("http://localhost:8080/customers/1",
    Customer.class);

for (Resource<Account> accountResource : customer.getAccounts()) {
  Account account = accountResource.getContent();
  Link accountTypeLink = accountResource.getLink("type");

  AccountType accountType = restTemplate.getForObject(accountTypeLink.getHref(),
      AccountType.class);

  System.out.format("%s: credit limit £%.2f%n", accountType.getName(),
      account.getCreditLimit());
}
```

All things going well, we should get the output:

```
Aardvantage: credit limit £2000.00
PlatyPlus: credit limit £1000.00
```

### Yikes ###

It works, but isn't this all a bit of a faff?
* It's *brittle* - we have to get links by their string names, and client code is tightly coupled to the structure of the JSON - something that can completely change just due to e.g. removing a Spring Data repository for an entity;
* It's *arcane* - knowing whether to map fields as List<Thing> or List<Resource<Thing>> can determine whether we have access to the data we require or not, and there are completely different ways of retrieving data from linked vs. inline associations; and
* It's *verbose* - having to continually invoke methods on the RestTemplate to load associations, and retrieve content and links from the Resource separately, makes for some very ugly code as the object graph grows in complexity.

go on about matchers

## A Better Way with HAL Client ##

Enter HAL Client. Our client is a wrapper around Spring HATEOAS and Jackson with a heavy JPA influence. Retrieving an object from the remote returns a *proxy* of the returned object, instrumented using Javassist, whose accessors can transparently conduct further remote service invocations in accordance with its HAL links. This allows linked and inline associations to both be defined in the same way in the client model and greatly simplifies client code.

Constructing a Client is easy. Configuration instances are immutable once created; ClientFactories and Clients maintain no mutable state internally and may be used safely by multiple threads.

```
ClientFactory clientFactory = Configuration.build().buildClientFactory();

Client<Customer> client = clientFactory.create(Customer.class);
```

### Our New Model ###

We need to update our Customer and Account classes for use with HAL Client.

```
public class Customer {
	private String name;
	private List<Account> accounts;

	@JsonDeserialize(contentUsing = InlineAssociationDeserializer.class)
	public List<Account> getAccounts() { return accounts; }

  // all other getters & setters
}
```

```
public class Account {
	private AccountType type;
	private BigDecimal creditLimit;

	@LinkedResource public AccountType getType() { return type; }

  // all other getters & setters
```

Things to note here:
* Spring HATEOAS Resource types are no longer required.
* Inline associations must specify Jackson deserialization with the InlineAssociationDeserializer. This tells Jackson to create a further proxy of the deserialized object, allowing nested linked associations to be resolved.
* Linked associations must specify the @LinkedResource annotation. This tells HAL Client proxies that the property must be populated by the result of a HAL link traversal on access.

### Simplified Client Code ###

So what does our client code look like now?

```
Customer customer = client.get(URI.create("http://localhost:8080/customers/1"));

for (Account account : customer.getAccounts()) {
	System.out.format("%s: credit limit £%.2f%n", account.getType().getName(),
			account.getCreditLimit());
}
```

Again giving:

```
Aardvantage: credit limit £2000.00
PlatyPlus: credit limit £1000.00
```

In this example, the invocation account.getType().getName() will automatically retrieve the resource identified by the "type" HAL link, returning the "name" property of that resource. Nice and tidy!


-- considerations --

* Jackson via field access probably better
