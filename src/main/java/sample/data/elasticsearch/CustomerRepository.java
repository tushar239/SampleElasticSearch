package sample.data.elasticsearch;

/**
 * Created by chokst on 3/15/15.
 */

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface CustomerRepository extends ElasticsearchRepository<Customer, String> {

    // find* methods can return found element(s) or Page or Slice. You can add Pageable and/or Sort as parameters along with actual searching parameters (like lastName, firstName etc).
    // find* methods can return List, Page or Slice
    public Customer findByFirstName(String firstName);

    public List<Customer> findByLastNameLike(String lastName);

    public List<Customer> findByLastNameContains(String lastName);

    public List<Customer> findByLastNameStartsWith(String lastName);

    public List<Customer> findByLastNameEndsWith(String lastName);

    // If your property names contain underscores (e.g. last_name) you can escape the underscore in the method name with a second underscore.
    // For a last_name property the query method would have to be named findByLast__name(…)
    public List<Customer> findByLastName(String lastName);


    /*
    creates the property traversal x.address.zipCode. The resolution algorithm starts with interpreting the entire part (AddressZipCode) as the property and checks the domain class for a property with that name (uncapitalized). If the algorithm succeeds it uses that property. If not, the algorithm splits up the source at the camel case parts from the right side into a head and a tail and tries to find the corresponding property, in our example, AddressZip and Code. If the algorithm finds a property with that head it takes the tail and continue building the tree down from there, splitting the tail up in the way just described. If the first split does not match, the algorithm move the split point to the left (Address, ZipCode) and continues.

    Although this should work for most cases, it is possible for the algorithm to select the wrong property. Suppose the Person class has an addressZip property as well. The algorithm would match in the first split round already and essentially choose the wrong property and finally fail (as the type of addressZip probably has no code property).

    To resolve this ambiguity you can use _ inside your method name to manually define traversal points. So our method name would end up like so:
     */
    //List<Customer> findByAddressZipCode(String zipCode);
    List<Customer> findByAddress_ZipCode(String zipCode);


    // The expressions are usually property traversals combined with operators that can be concatenated.
    // You can combine property expressions with AND and OR.
    // You also get support for operators such as Between, LessThan, GreaterThan, Like for the property expressions.
    List<Customer> findByAddressAndLastName(Address emailAddress, String lastName);

    // Enables the distinct flag for the query ??? Don't know how it works --- as per my understanding it should be a union of findDistinctCustomersByLastName and findDistinctCustomersByFirstName, but it is not.
    List<Customer> findDistinctCustomersByLastNameOrFirstName(String lastName, String firstName);
    List<Customer> findCustomersDistinctByLastNameOrFirstName(String lastName, String firstName);

    // Enabling ignoring case for an individual property
    List<Customer> findByLastNameIgnoreCase(String lastName);
    // Enabling ignoring case for all suitable properties
    List<Customer> findByLastNameAndFirstNameAllIgnoreCase(String lastName, String firstName);

    // Enabling static ORDER BY for a query
    List<Customer> findByLastNameOrderByFirstNameAsc(String lastName);
    List<Customer> findByLastNameOrderByFirstNameDesc(String lastName);



    // deleteBy* returns id as a return type
    public List<Customer> deleteByLastName(String lastName);

    // Not working --- as per documentation, deleteBy and removeBy have same effect, but removeBy is not working
    public List<Customer> removeByLastName(String lastName);


    Long countByLastName(String lastName);


    // Limiting query results
    Customer findFirstByOrderByLastNameAsc();
    Customer findTopByOrderByLastNameDesc();
    Page<Customer> queryFirst10ByLastName(String lastName, Pageable pageable);
    Slice<Customer> findTop3ByLastName(String lastName, Pageable pageable);
    List<Customer> findFirst10ByLastName(String lastName, Sort sort);
    List<Customer> findTop10ByLastName(String lastName, Pageable pageable);


    // If Query methods are not available, you can use actual elastic search query in @Query annotation or Filter Builder

    // @Query annotation
    // @Query("{"bool" : {"must" : {"field" : {"name" : "?0"}}}}")
    //Page<Customer> findByName(String name,Pageable pageable);

    //OR
    // Filter Builder
    /*
    private ElasticsearchTemplate elasticsearchTemplate;

SearchQuery searchQuery = new NativeSearchQueryBuilder()
    .withQuery(matchAllQuery())
    .withFilter(boolFilter().must(termFilter("id", documentId)))
    .build();

Page<SampleEntity> sampleEntities =
    elasticsearchTemplate.queryForPage(searchQuery,SampleEntity.class);
     */


    /*
    {
      "query": {  --- same as @Query
        "query_string": {
          "query": "?0"  --- first parameter of method
        }
      }
    }
     */
    @Query("{\"query_string\": {\"query\": \"?0\"}}")
    List<Customer> searchAllFields(String toBeFound);

    /*
    {
        "query": {   --- same as @Query
            "query_string": {
                "query": "?0", --- first parameter of method
                "fields": ["firstName"]
            }
        }
    }

     */
    @Query("{ \"query_string\": { \"query\": \"?0\", \"fields\": [\"firstName\"] } }")
    List<Customer> findByFname(String firstName);

}