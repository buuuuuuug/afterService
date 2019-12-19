package xmu.ooad.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import xmu.ooad.AftersaleApplication;
import xmu.ooad.domain.AfterSale;
import xmu.ooad.util.JacksonUtil;

import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = AftersaleApplication.class)
@AutoConfigureMockMvc
@Transactional
class AfterSaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 管理员查询某一个用户所有的售后记录
     * 会返回所有售后记录（包括已被删除的）的所有信息（包括售后记录是否被删除信息）
     * 如果userId不存在，会返回一个空列表，而不是参数错误----我的理解
     */
    @Test
    void adminFindAfterSalesServiceList() throws Exception {

        String responseString = this.mockMvc.perform(get("/admin/afterSaleServices").param("userId","1").param("limit", "10").param("page","10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        System.out.println(responseString);

    }

    /**
     * 管理员根据id查询售后记录
     * 如果对应id下的售后记录不存在，返回401：参数不对
     */
    @Test
    void adminFindAfterSaleServiceById() throws Exception {
        Integer id=111;
        String responseString = this.mockMvc.perform(get("/admin/afterSaleServices/{id}",id))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        System.out.println(responseString);
    }

    /**
     * 管理员修改售后记录，只能修改statusCode，不能修改其他属性
     * 只有管理员未审核时才能审核，审核后不能二次审核
     * 管理员只有两种审核状态：审核成功1 和 审核失败2，没有其他状态
     * 问题：在测试中，statusCode=null，但是在postman中statusCode正常！！！
     * 问题解决：使用mockMvc模拟http请求时，发送的是application/json格式的content
     *          此时需要@requestBody
     *          在使用postman发送http请求时，发送的是application/application/x-www-form-urlencoded格式的content
     *          此时不需要@requestBody
     *          https://github.com/spring-projects/spring-framework/issues/22734
     */
    @Test
    void adminUpdateAfterSaleService() throws Exception {
        AfterSale afterSale = new AfterSale();
        afterSale.setStatusCode(9);

        String jsonString = JacksonUtil.toJson(afterSale);
        System.out.println(jsonString);

        Integer id=5;
        String responseString = this.mockMvc.perform(put("/admin/afterSaleServices/{id}",id).content(jsonString).contentType("application/json;charset=utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        System.out.println(responseString);

    }

    /**
     * 用户根据userId查找售后记录
     */
    @Test
    void userFindAfterSaleServiceList() throws Exception {
        Integer userId=1;
        Integer limit=10;
        Integer page=10;

        String responseString = this.mockMvc.perform(get("/afterSaleService").param("userId", String.valueOf(userId)).param("limit", String.valueOf(limit)).param("page", String.valueOf(page)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        System.out.println(responseString);

    }

    /**
     * 用户根据id查找售后记录
     */
    @Test
    void userFindAfterSaleService() throws Exception {

        Integer userId=1;
        Integer id=1;

        String responseString = this.mockMvc.perform(get("/afterSaleService/{id}",id).param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        System.out.println(responseString);
    }

    /**
     * 用户申请售后服务，需要提供
     *   goodsType   只能是1，2，3，4
     *   applyReason
     *   type     只能是0，1
     *   number
     *   orderItemId
     *   userId
     */
    @Test
    void userApplyAfterSaleService() throws Exception {
        Integer userId=88;
        AfterSale afterSale = new AfterSale();
        afterSale.setGoodsType(1);
        afterSale.setApplyReason("测试--申请理由");
        afterSale.setType(1);
        afterSale.setNumber(1);
        afterSale.setOrderItemId(99);

        String jsonString = JacksonUtil.toJson(afterSale);

        String responseString = this.mockMvc.perform(post("/afterSaleService").param("userId", String.valueOf(userId)).content(jsonString).contentType("application/json;charset=utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        System.out.println(responseString);
    }

    /**
     * 用户修改售后
     * @throws Exception
     */
    @Test
    void userUpdateAfterSaleService() throws Exception {
        Integer id=5;
        AfterSale afterSale = new AfterSale();
        afterSale.setApplyReason("增加后的理由");
        //afterSale.setBeApplied(false);
        afterSale.setUserId(99);

        String jsonString = JacksonUtil.toJson(afterSale);

        String responseString = this.mockMvc.perform(put("/afterSaleService/{id}",id).content(jsonString).contentType("application/json;charset=utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        System.out.println(responseString);

    }

    /**
     * 用户删除售后申请
     * @throws Exception
     */
    @Test
    void userDeleteAfterSaleService() throws Exception {

        Integer id=1;

        String responseString = this.mockMvc.perform(delete("/afterSaleService/{id}",id))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        System.out.println(responseString);
    }
}