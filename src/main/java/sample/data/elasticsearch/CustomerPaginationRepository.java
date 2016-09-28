package sample.data.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * Created by chokst on 3/29/15.
 */
public interface CustomerPaginationRepository extends ElasticsearchRepository<Customer, String> {

    // Page interface extends Slice interface
    Page<Customer> findByLastName(String lastName, Pageable pageable);

    // Default Sort is by FIELD_SCORE.
    List<Customer> findByLastName(String lastName, Sort sort);

}
