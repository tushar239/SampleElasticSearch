package sample.elasticsearch.client;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsFilterBuilder;
import org.elasticsearch.index.query.WrapperQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

import java.util.Map;

/**
 * @author Tushar Chokshi @ 9/28/15.
 */
public class ElasticSearchClientTest {
    public static void main(String[] args) {
        // on startup

        // dst01.${deployment.env}.cobaltgroup.com:10300,dst02.${deployment.env}.cobaltgroup.com:10300

        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "cobaltsearch-dev-sea").build();

        Client client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("dst01.dev-sea.cobaltgroup.com", 10300))
                .addTransportAddress(new InetSocketTransportAddress("dst02.dev-sea.cobaltgroup.com", 10300));
        /*{

            SearchResponse searchResponse = client.prepareSearch("incentives").
                    setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.termQuery("webId", "gmps-86th-street")).execute().actionGet();

            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit searchHit : searchHits) {
                Map<String, Object> source = searchHit.getSource();
                for (String sourceKey : source.keySet()) {
                    System.out.println("key:" + sourceKey + " ,value:" + source.get(sourceKey));
                }

            }
        }*/

        /*{
            final QueryBuilder queryBuilder = new MatchAllQueryBuilder();
            final FilterBuilder andFilterBuilder = new AndFilterBuilder(new TermsFilterBuilder("webId", "gmps-86th-street"), new TermsFilterBuilder("locale", "en_US"));
            final FilteredQueryBuilder filteredQueryBuilder = new FilteredQueryBuilder(queryBuilder, andFilterBuilder);

            SearchRequestBuilder searchRequestBuilder = client.prepareSearch("incentives").setQuery(filteredQueryBuilder).setFetchSource(new String[]{"incentiveId", "hashCode"}, null); //include only incentiveId and hashCode in the response
            System.out.println("Search query:"+searchRequestBuilder.toString());
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

            System.out.println("Time Taken in ms:" + searchResponse.getTookInMillis());
            System.out.println("Search Response:"+searchResponse.toString());
            for (SearchHit searchHit : searchResponse.getHits().getHits()) {
                Map<String, Object> source = searchHit.getSource();
                for (String sourceKey : source.keySet()) {
                    System.out.println("key:" + sourceKey + " ,value:" + source.get(sourceKey));
                }

            }

        }*/

        {
            final QueryBuilder queryBuilder = new MatchAllQueryBuilder();
            final BoolFilterBuilder boolFilterBuilder = new BoolFilterBuilder().must(new TermsFilterBuilder("webId", "gmps-86th-street"), new TermsFilterBuilder("locale", "en_US"));


            final FilteredQueryBuilder filteredQueryBuilder = new FilteredQueryBuilder(queryBuilder, boolFilterBuilder);

            SearchRequestBuilder searchRequestBuilder = client.prepareSearch("incentives").setQuery(filteredQueryBuilder).setFetchSource(new String[]{"incentiveId", "hashCode"}, null); //include only incentiveId and hashCode in the response
            System.out.println("Search query:"+searchRequestBuilder.toString());
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

            System.out.println("Time Taken in ms:" + searchResponse.getTookInMillis());
            System.out.println("Search Response:" + searchResponse.toString());
            for (SearchHit searchHit : searchResponse.getHits().getHits()) {
                Map<String, Object> source = searchHit.getSource();
                for (String sourceKey : source.keySet()) {
                    System.out.println("key:" + sourceKey + " ,value:" + source.get(sourceKey));
                }

            }

        }
        // WrapperQueryBuilder - http://rajish.github.io/api/elasticsearch/0.20.0.Beta1-SNAPSHOT/org/elasticsearch/index/query/WrapperQueryBuilder.html
        {

            BoolQueryBuilder bool = new BoolQueryBuilder();
            bool.must(new WrapperQueryBuilder("{\"term\": {\"webId\":\"gmps-21\"}}"));
            bool.must(new TermQueryBuilder("locale", "en_US"));
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch("incentives").setQuery(bool);
            System.out.println("Search query:" + searchRequestBuilder.toString());
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
            for (SearchHit searchHit : searchResponse.getHits().getHits()) {
                Map<String, Object> source = searchHit.getSource();
                for (String sourceKey : source.keySet()) {
                    System.out.println("key:" + sourceKey + " ,value:" + source.get(sourceKey));
                }

            }
        }
            {
            String query = "{\"filtered\":{\"query\":{\"match_all\":{}},\"filter\":{\"bool\":{\"must\":[{\"terms\":{\"webId\":[\"gmps-21\"]}},{\"terms\":{\"locale\":[\"en_US\"]}}]}}}}";
            final WrapperQueryBuilder queryBuilder = new WrapperQueryBuilder(query);
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch("incentives").setQuery(queryBuilder).addSort("createdDate", SortOrder.DESC).addSort("startDate", SortOrder.DESC).setFrom(0).setSize(29);

            System.out.println("Search query:"+searchRequestBuilder.toString());
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

            System.out.println("Time Taken in ms:" + searchResponse.getTookInMillis());
            System.out.println("Search Response:" + searchResponse.toString());
            for (SearchHit searchHit : searchResponse.getHits().getHits()) {
                Map<String, Object> source = searchHit.getSource();
                for (String sourceKey : source.keySet()) {
                    System.out.println("key:" + sourceKey + " ,value:" + source.get(sourceKey));
                }

            }
        }

        // SearchResponse response = client.prepareSearch().execute().actionGet();
// on shutdown

        client.close();
    }
}
