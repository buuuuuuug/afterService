package xmu.ooad.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import xmu.ooad.domain.AfterSale;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AfterSaleMapperTest {

    @Autowired
    AfterSaleMapper afterSaleMapper;

    /**
     * 管理员通过userId查找售后记录
     */
    @Test
    void findAfterSales() {
        Integer userId=1;
        List<AfterSale> afterSales = afterSaleMapper.findAfterSales(userId);
        for (AfterSale a : afterSales) {
            System.out.println(a.toString());
        }
    }

    /**
     * 管理员通过id 查找售后记录
     */
    @Test
    void findAfterSaleById() {
        Integer id=1;
        AfterSale afterSale = afterSaleMapper.findAfterSaleById(id);
        assertEquals(id,afterSale.getId());
        System.out.println(afterSale);
    }

    /**
     * 用户通过usrId 查找售后记录
     */
    @Test
    void findAfterSalesByUserId() {
        Integer userId=1;
        List<AfterSale> afterSales = afterSaleMapper.findAfterSalesByUserId(userId);
        for (AfterSale a : afterSales) {
            System.out.println(a.toString());
        }
    }

    /**
     * 更改售后记录
     */
    @Test
    void updateAfterSale() {
        AfterSale afterSale = new AfterSale();
        afterSale.setId(1);
        afterSale.setApplyReason("mapper--修改后的理由");

        int res = afterSaleMapper.updateAfterSale(afterSale);
        System.out.println(res);
    }

    /**
     * 增加售后记录
     */
    @Test
    void insertAfterSale() {
        AfterSale afterSale = new AfterSale();
        afterSale.setUserId(88);
        afterSale.setApplyReason("mapper--修改后的理由");
        afterSale.setBeApplied(true);
        afterSale.setGoodsType(1);
        afterSale.setNumber(1);
        afterSale.setType(0);

        int res = afterSaleMapper.insertAfterSale(afterSale);
        System.out.println(res);
    }

    @Test
    void deleteAfterSale() {
        Integer id = 1;
        int res = afterSaleMapper.deleteAfterSale(id);
        System.out.println(res);
    }
}