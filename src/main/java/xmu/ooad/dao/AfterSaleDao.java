package xmu.ooad.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import xmu.ooad.domain.AfterSale;
import xmu.ooad.mapper.AfterSaleMapper;

import java.util.List;

/**
 * @author 水木子
 */
@Repository
public class AfterSaleDao {

    @Autowired
    private AfterSaleMapper afterSaleMapper;

    public List<AfterSale> findAfterSales(Integer userId){
        return afterSaleMapper.findAfterSales(userId);
    }

    public AfterSale findAfterSaleById(Integer id){
        return afterSaleMapper.findAfterSaleById(id);
    }

    public List<AfterSale> findAfterSalesByUserId(Integer userId){
        return afterSaleMapper.findAfterSalesByUserId(userId);
    }

    public AfterSale updateAfterSale(AfterSale afterSale){
        afterSaleMapper.updateAfterSale(afterSale);
        return afterSaleMapper.findAfterSaleById(afterSale.getId());
    }

    public AfterSale insertAfterSale(AfterSale afterSale){
        afterSaleMapper.insertAfterSale(afterSale);
        return afterSaleMapper.findAfterSaleById(afterSale.getId());
    }

    public int deleteAfterSale(Integer id){
        return afterSaleMapper.deleteAfterSale(id);
    }
}
