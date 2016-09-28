package sample.data.elasticsearch;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by chokst on 3/29/15.
 */
public interface CustomerSlicingRepository extends ElasticsearchRepository<Customer, String> {

    // A Page knows about the total number of elements and pages available. It does so by the infrastructure triggering a count query to calculate the overall number.
    // As this might be expensive depending on the store used, Slice can be used as return instead. A Slice only knows about whether there’s a next Slice available which might be just sufficient when walking thought a larger result set.
    Slice<Customer> findByLastName(String lastName, Pageable pageable);

}
