package net.dgg.accountingtools;

import net.dgg.accountingtools.common.User;
import net.dgg.framework.tac.elasticsearch.DggESTemplate;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: huanggy
 * @date: 2019/2/25
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class ESTest {

    @Autowired
    private DggESTemplate esTemplate;

    private List<User> list = new ArrayList<>();

    @Before
    public void getUserList(){
        list.add(new User(1001L, "Milk", "111111", new Date(), 23, "美国纽约"));
        list.add(new User(1002L, "Marry", "111111", new Date(), 24, "中国成都"));
        list.add(new User(1003L, "July", "111111", new Date(), 22, "美国华盛顿"));
        list.add(new User(1004L, "Rose", "111111", new Date(), 26, "成都成华区"));
        list.add(new User(1005L, "Jim", "111111", new Date(), 23, "成都武侯区"));
        list.add(new User(1006L, "Bob", "111111", new Date(), 28, "成都金牛区"));
        list.add(new User(1007L, "Susan", "111111", new Date(), 33, "成都磨子桥"));
        list.add(new User(1008L, "John", "111111", new Date(), 32, "成都双流县"));
        list.add(new User(1009L, "Jack", "111111", new Date(), 18, "成都华阳"));
        list.add(new User(1010L, "Jet", "111111", new Date(), 20, "成都诺克萨斯"));
        list.add(new User(1011L, "Jim", "111111", new Date(), 23, "美国休斯顿"));
        list.add(new User(1012L, "小明", "111111", new Date(), 43, "英国伦敦"));
        list.add(new User(1013L, "小红", "111111", new Date(), 13, "法国巴黎"));
        list.add(new User(1014L, "小张", "111111", new Date(), 15, "小日本东京"));
        list.add(new User(1015L, "隔壁老王", "111111", new Date(), 40, "韩国汉城"));
    }

    /**
     * 创建索引
     * @throws Exception
     */
    @Test
    public void createIndex() throws Exception {
        CreateIndexResponse rep = esTemplate.createIndex("huanggy_test", 5, 1);
        System.out.println("索引名称： " + rep.index());
    }

    /**
     * 通过 id 查询
     * @throws Exception
     */
    @Test
    public void findById() throws Exception {
        GetResponse singleById = esTemplate.getSingleById("huanggy_test", "user", "1001");
        Map<String, Object> sourceAsMap = singleById.getSourceAsMap();
        String sourceAsString = singleById.getSourceAsString();
        System.out.println(sourceAsString );
    }

    /**
     * 插入数据
     * @throws Exception
     */
    @Test
    public void insert() throws Exception {
        User user = new User(50001L , "HanMeimei", "111111", new Date(), 27, "成都钻石广场");
        
        // 插入数据
        IndexResponse indexResponse = esTemplate.insertDocment("huanggy_test", "user", user);

        // 指定 ID，实体中会有 ID 这里可以不写
        esTemplate.insertDocment("huanggy_test", "user", "1002", user);
		
        System.out.println(indexResponse.status().getStatus()); // 201
    }

    /**
     * 批量插入
     * @throws Exception
     */
    @Test
    public void bulkInsert() throws Exception {
        BulkResponse bulkItemResponses = esTemplate.bulkInsert("huanggy_test", "user", list);
        System.out.println(bulkItemResponses.status().getStatus()); // 200
    }

    /**
     * 通过 ID 删除
     * @throws Exception
     */
    @Test
    public void deleteById() throws Exception {
        DeleteResponse deleteResponse = esTemplate.deleteById("huanggy_test", "user", "50001");
        System.out.println(deleteResponse.status().getStatus()); // 200
    }

    /**
     * 批量删除，String 类型的集合，放入 ID 即可
     * @throws Exception
     */
    @Test
    public void bulkDelete() throws Exception {
        ArrayList<String> userIdList = new ArrayList<>();
        for (User u : list){
            userIdList.add(String.valueOf(u.getId()));
        }
        BulkResponse bulkItemResponses = esTemplate.bulkDelete("huanggy_test", "user", userIdList);
        System.out.println(bulkItemResponses.status().getStatus());
    }

    /**
     * 通过 ID 更新，更新一条数据
     * @throws Exception
     */
    @Test
    public void updateId() throws Exception {
        User user = new User(50001L, "盖伦", "222222", new Date(), 38, "德玛西亚");
        UpdateResponse updateResponse = esTemplate.updateById("huanggy_test", "user", "50001", user);
        System.out.println(updateResponse.status().getStatus()); // 200
    }

    /**
     * 批量更新
     * @throws Exception
     */
    @Test
    public void bulkUpdate() throws Exception {
        for (User user : list) {
            user.setPassword("222222");
        }
        BulkResponse bulkItemResponses = esTemplate.bulkUpdate("huanggy_test", "user", list);
        System.out.println(bulkItemResponses.status().getStatus());
    }

    /**
     * 条件搜索
     * @throws Exception
     */
    @Test
    public void search() throws Exception {

        // 构建必要条件：必须包含 username 且 值为 Milk2 ; 必须包含 password 且值为 22
        MatchPhraseQueryBuilder term1 = QueryBuilders.matchPhraseQuery("password", "111111");
        MatchPhraseQueryBuilder term2 = QueryBuilders.matchPhraseQuery("userName", "Milk");

        // 查不到数据，也搞不明白为什么，fuck
        // TermQueryBuilder term3 = QueryBuilders.termQuery("addr.keyword", "美国");

        // 匹配查询：addr 值必须包含 成都；*：任意多个个字符；?：任意一个字符
        WildcardQueryBuilder term7 = QueryBuilders.wildcardQuery("addr", "*国*");

        // 范围查询：10 <= age <= 30 ；includeXXX 包含临界值
        RangeQueryBuilder term4 = QueryBuilders.rangeQuery("age").gt("10").lt("30").includeLower(true).includeUpper(true);

        // 必须包含 born 字段
        ExistsQueryBuilder term5 = QueryBuilders.existsQuery("born");

        // 没有 sex 字段 或 sex 值为 null 或空字符串; 配合 bool 查询 mustNot() 完成
        ExistsQueryBuilder term6 = QueryBuilders.existsQuery("sex");

        // 构建条件查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 条件塞进查询对象
        BoolQueryBuilder mustTerms = boolQueryBuilder.must(term1).must(term2).must(term4).must(term5).mustNot(term6).must(term7);

        // 第 5 条开始取 5 条数据，按照 born 字段 顺序排序
        SearchSourceBuilder query = new SearchSourceBuilder().query(mustTerms).size(5).sort("born", SortOrder.ASC).from(0);

        // 开始查询
        SearchResponse rep = esTemplate.query("huanggy_test", "user", query);

        // 获取命中结果集
        SearchHits hits = rep.getHits();

        System.out.println("命中总条数 ：" + hits.totalHits);

        // 遍历命中
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsMap().toString());
        }

    }


}
