package sample.data.elasticsearch;

/**
 * Created by chokst on 3/15/15.
 */

// https://github.com/spring-projects/spring-boot/tree/1.2.x/spring-boot-samples/spring-boot-sample-data-elasticsearch
// http://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/
// https://github.com/spring-projects/spring-data-elasticsearch

import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

@SpringBootApplication // OR use @EnableElasticsearchRepositories(basePackages = "sample.data.elasticsearch")
public class SampleElasticsearchApplication implements CommandLineRunner {

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private CustomerPaginationRepository customerPaginationRepository;

    @Autowired
    private CustomerSlicingRepository customerSlicingRepository;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SampleElasticsearchApplication.class, "--debug").close();
    }

    @Override
    public void run(String... args) throws Exception {
        this.repository.deleteAll();
        saveCustomers();
        checkExistance();
        deleteCustomer();
        findOne();
        countCustomers();
        fetchAllCustomers();
        fetchIndividualCustomers();
        fetchCustomersByLastNameLike();
        findDistinctCustomerByLastNameOrFirstName();
        fetchCustomersForZipCode();
        fetchPaginatedCustomers();
        fetchPaginatedCustomersByLastName();
        fetchSortedCustomers();
        //fetchSlicedCustomersByLastName();

        searchInAllFieldsUsingQueryAnnotation();
    }

    private void saveCustomers() {
        {
            Customer customer = new Customer("Alice", "Smith");
            Address address = new Address();
            address.setCity("Sacramento");
            address.setState("CA");
            address.setZipCode("97469");
            customer.setAddress(address);
            this.repository.save(customer);
        }

        this.repository.save(new Customer("Bob", "Smith"));
        this.repository.save(new Customer("Bob", "Rohtak"));// This will overwrite a record with id(firstName)="Bob" if previously inserted
        this.repository.save(new Customer("Tushar", "Chokshi"));
        this.repository.save(new Customer("Miral", "Chokshi"));
        this.repository.save(new Customer("Srikant", "Chokshi"));
        this.repository.save(new Customer("Yogesh", "Chokshi"));
        this.repository.save(new Customer("Mayur", "Chokshi"));
        this.repository.save(new Customer("Jagdish", "Chokshi"));
        this.repository.save(new Customer("Darshana", "Chokshi"));
        this.repository.save(new Customer("Harshil", "Chokshi"));
        this.repository.save(new Customer("Krunal", "Chokshi"));
        this.repository.save(new Customer("Narendrabhai", "Patel"));
        this.repository.save(new Customer("Savitriben", "Patel"));
        this.repository.save(new Customer("Sejal", "Patel"));
        this.repository.save(new Customer("Rupal", "Patel"));
        this.repository.save(new Customer("Deval", "Patel"));
    }
    private void deleteCustomer() {
        System.out.println("--------------------------------");
        System.out.println("deleting/removing customer(s):");
        System.out.println("-------------------------------");

        Customer customer = new Customer();
        customer.setFirstName("Bob"); // for delete() id field is mandatory
        //customer.setLastName("Smith"); // has no effect.
        repository.delete(customer);

        List<Customer> deletedCustomers = repository.deleteByLastName("Smith");
        for (Customer deletedCustomer : deletedCustomers) {
            System.out.println(deletedCustomer.getFirstName() +" is deleted");
        }

        // Not working --- as per documentation, deleteBy and removeBy have same effect, but removeBy is not working
        /*List<Customer> removedCustomers = repository.removeByLastName("Patel");
        for (Customer removedCustomer : removedCustomers) {
            System.out.println(removedCustomer.getFirstName() +" is removed");
        }*/

    }

    private void countCustomers() {
        System.out.println("--------------------------------");
        System.out.println("Count customers by last name:");
        System.out.println("Customers count by last name: "+repository.countByLastName("Chokshi"));
    }

    private void checkExistance() {
        System.out.println("--------------------------------");
        System.out.println("Check for existance of a customer:");
        System.out.println("Customer Tushar exists? = "+repository.exists("Tushar")); // exists(ID) needs id as a parameter
    }

    private void fetchAllCustomers() {
        System.out.println("--------------------------------");
        System.out.println("Customers found with findAll():");
        System.out.println("-------------------------------");
        printCustomers(this.repository.findAll());
    }

    private void findOne() {
        System.out.println("--------------------------------");
        System.out.println("Customer found with findOne('Tushar'):");
        Customer customer = repository.findOne("Tushar"); // just like delete(), findOne also needs id.
        printCustomer(customer);
    }

    private void fetchIndividualCustomers() {
        System.out.println("--------------------------------");
        System.out.println("Customer found with findByFirstName('Alice'):");
        {
            Customer customer = this.repository.findByFirstName("Alice");
            printCustomer(customer);
        }
        {
            System.out.println("Customers found with findByLastName('Smith'):");
            printCustomers(this.repository.findByLastName("Smith"));
        }
    }

    private void fetchCustomersByLastNameLike() {
        System.out.println("--------------------------------");
        System.out.println("Fetch Customers by last name like:");
        printCustomers(repository.findByLastNameLike("cho"));// "hok"/ "kshi" didn't work

        System.out.println("--------------------------------");
        System.out.println("Fetch Customers last name containing:");
        printCustomers(repository.findByLastNameContains("hok")); // worked

        System.out.println("--------------------------------");
        System.out.println("Fetch Customers last name start with:");
        printCustomers(repository.findByLastNameStartsWith("Pat")); // worked

        System.out.println("--------------------------------");
        System.out.println("Fetch Customers last name ends with:");
        printCustomers(repository.findByLastNameEndsWith("tel")); // worked

    }

    private void findDistinctCustomerByLastNameOrFirstName() {
        System.out.println("--------------------------------");
        {
            System.out.println("Find distinct customers by lastName (Chokshi) or firstName (Sejal):");
            printCustomers(repository.findDistinctCustomersByLastNameOrFirstName("Chokshi", "Sejal"));
        }
        {
            System.out.println("Find distinct customers by lastName (notExist) or firstName (Sejal):");
            printCustomers(repository.findDistinctCustomersByLastNameOrFirstName("notExist", "Sejal"));
        }

    }

    private void fetchCustomersForZipCode() {
        System.out.println("--------------------------------");
        System.out.println("Fetch customers with zipcode");
        printCustomers(repository.findByAddress_ZipCode("97469"));
    }

    private void fetchPaginatedCustomers() {
        System.out.println("--------------------------------");
        System.out.println("Fetching paginated customers");
        Page<Customer> page = this.customerPaginationRepository.findAll(new PageRequest(1, 5));// find 5 records for page#2.
        System.out.println("Total Pages:" + page.getTotalPages());// total pages
        System.out.println("Total Elements:" +page.getTotalElements());// total records in Customer index
        List<Customer> customers = page.getContent();
        printCustomers(customers);
    }


    private void fetchPaginatedCustomersByLastName() {
        System.out.println("--------------------------------");
        System.out.println("Fetching paginated customers by last name");
        System.out.println("--------------------------------");
        Page<Customer> page = this.customerPaginationRepository.findByLastName("Chokshi", new PageRequest(1, 5));
        System.out.println("Total Pages:" + page.getTotalPages());// total pages with last name=Chokshi
        System.out.println("Total Elements:" +page.getTotalElements());// Total records in Customer index with last name=Chokshi
        List<Customer> customers = page.getContent();
        printCustomers(customers);
    }


    private void fetchSlicedCustomersByLastName() {
        System.out.println("--------------------------------");
        System.out.println("Fetching sliced customers by last name");

        Slice<Customer> slice = this.customerSlicingRepository.findByLastName("Chokshi", new PageRequest(1, 5));
        if(slice.hasContent()) {
            List<Customer> customers = slice.getContent();

            printCustomers(customers);

            if (slice.hasNext()) {
                Pageable pageable = slice.nextPageable();
                System.out.println(String.format("Next Page Info: Page Number=%s, Page Offset=%s, Page Size=%s", pageable.getPageNumber(), pageable.getOffset(), pageable.getPageSize()));
            }
            if (slice.hasPrevious()) {
                Pageable pageable = slice.previousPageable();
                System.out.println(String.format("Previous Page Info: Page Number=%s, Page Offset=%s, Page Size=%s", pageable.getPageNumber(), pageable.getOffset(), pageable.getPageSize()));
            }
        }
    }

    // https://github.com/BioMedCentralLtd/spring-data-elasticsearch-sample-application/blob/master/src/test/java/org/springframework/data/elasticsearch/repositories/SampleBookRepositoryTest.java
    private void fetchSortedCustomers() {
        System.out.println("--------------------------------");
        System.out.println("Fetching sorted customers found by last name and sorted by first name");
        printCustomers(customerPaginationRepository.findByLastName("Chokshi", new Sort(new Sort.Order(Sort.Direction.ASC,"firstName"))));
    }

    private void searchInAllFieldsUsingQueryAnnotation() {
        System.out.println("--------------------------------");
        System.out.println("Search in all fields using query annotation");
        printCustomers(repository.searchAllFields("Sacramento"));

        System.out.println("Search in FirstName field using query annotation");
        printCustomers(repository.findByFname("Tushar"));
    }

    private void printCustomer(Customer customer) {
        System.out.println("First Name:" + customer.getFirstName() + ", Last Name:" + customer.getLastName());
    }
    private void printCustomers(List<Customer> customers) {
        for (Customer customer : customers) {
            System.out.println("First Name:" + customer.getFirstName() + ", Last Name:" + customer.getLastName());
        }
    }
    private void printCustomers(Iterable<Customer> customers) {
        for (Customer customer : customers) {
            System.out.println("First Name:" + customer.getFirstName() + ", Last Name:" + customer.getLastName());
        }
    }


}