package xmu.ooad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xmu.ooad.dao.AfterSaleDao;
import xmu.ooad.domain.AfterSale;
import xmu.ooad.service.AfterSaleService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author 水木子
 */
@Service
public class AfterSaleServiceImpl implements AfterSaleService {

    @Autowired
    private AfterSaleDao afterSaleDao;

    @Override
    public List<AfterSale> findAfterSales(Integer userId) {
        return afterSaleDao.findAfterSales(userId);
    }

    @Override
    public AfterSale findAfterSaleById(Integer id) {
        return afterSaleDao.findAfterSaleById(id);
    }

    @Override
    public List<AfterSale> findAfterSalesByUserId(Integer userId) {
        return afterSaleDao.findAfterSalesByUserId(userId);
    }

    /**
     * 管理员修改售后记录，应该只能修改审核状态
     * 首先根据id从数据库中读取售后记录，然后修改对象的相关属性，再修改到数据库中
     * @param afterSale
     * @return 返回修改后的对象
     */
    @Override
    public AfterSale adminUpdateAfterSale(AfterSale afterSale) {
        AfterSale afterSale1 = afterSaleDao.findAfterSaleById(afterSale.getId());
        //只有根据id能找到这个售后记录，并且用户没有取消，管理员才审核
        if(afterSale1!=null && afterSale1.getBeApplied()){
            int i=afterSale.getStatusCode();
            //只有管理员没审核过的售后记录，才进行审核，不能进行二次审核
            if(afterSale1.getStatusCode()==0 && (i==1 ||i==2)) {
                //管理员修改审核状态
                afterSale1.setStatusCode(afterSale.getStatusCode());
                //设置最近修改时间
                afterSale1.setGmtModified(LocalDateTime.now());
                //要设置结束时间吗？
                afterSale1.setEndTime(LocalDateTime.now());
                //返回修改后的售后记录
                return afterSaleDao.updateAfterSale(afterSale1);
            }else{
                return null;
            }
        }
        else{
            return null;
        }
    }

    @Override
    public AfterSale updateAfterSale(AfterSale afterSale) {
        //首先根据id获取数据库中的售后记录
        AfterSale afterSale1 = afterSaleDao.findAfterSaleById(afterSale.getId());
        //比较afterSale1 中的userId 和 afterSale 中的userId是否相同
        //不同表示A用户修改了B用户的售后记录 ，此时修改 afterSale 的userId，然后返回afterSale
        //数据库中有相应的记录
        if (afterSale1 != null) {
            //管理员还没有审核并且该售后没有被删除\被取消
            if(afterSale1.getStatusCode()==0 && !afterSale1.getBeDeleted() && afterSale1.getBeApplied()) {
                //用户可以修改number，applyReason,type,beApplied，goodsType
                //设置最近修改时间
                afterSale1.setGmtModified(LocalDateTime.now());
                //用户可以取消售后
                if(afterSale.getBeApplied()!=null && !afterSale.getBeApplied()) {
                    afterSale1.setBeApplied(afterSale.getBeApplied());
                }
                //用户可以增加申请理由
                if(afterSale.getApplyReason()!=null){
                    afterSale1.setApplyReason(afterSale1.getApplyReason()+' '+afterSale.getApplyReason());
                }
                //用户可以更改售后类型
                if(afterSale.getType()!=null && (afterSale.getType()==0 || afterSale.getType()==1)){
                    afterSale1.setType(afterSale.getType());
                }
                //用户可以修改售后数量
                if(afterSale.getNumber()!=null && afterSale.getNumber()>0){
                    afterSale1.setNumber(afterSale.getNumber());
                }
                //用户可以修改售后商品类型
                if(afterSale.getGoodsType()!=null) {
                    int goodsType = afterSale.getGoodsType();
                    if (goodsType >= 1 && goodsType <= 4) {
                        afterSale1.setGoodsType(goodsType);
                    }
                }
                //返回更新后的售后记录
                return afterSaleDao.updateAfterSale(afterSale1);
            } else if(afterSale1.getStatusCode()!=0 && !afterSale1.getBeDeleted() && afterSale1.getBeApplied()){
                //管理员审核后的售后记录、并且该订单还没有被删除、取消
                //用户只能取消售后
                if(afterSale.getBeApplied()==false){
                    afterSale1.setBeApplied(afterSale.getBeApplied());
                    return afterSaleDao.updateAfterSale(afterSale1);
                }else{
                    return null;
                }
            }else{
                //售后记录被删除、被取消，用户都不能再修改 售后记录了
                return null;
            }
        }else{
            //数据库中没有相应的售后记录
            return null;
        }
    }

    @Override
    public AfterSale insertAfterSale(AfterSale afterSale) {
        //首先通过orderItemId,调取order服务的接口，返回该orderItem
        //初步判断是否接受该订单
        LocalDateTime orderCreateTime = LocalDateTime.now();
        LocalDateTime now=LocalDateTime.now();
        Duration duration = Duration.between(orderCreateTime,now);
        //售后申请在订单有效期内（7天）
        if(duration.toNanos()<7*24*60*60*1000){
            //设置管理员审核状态，0代表未审核
            afterSale.setStatusCode(0);
            //设置是否申请，true代表是
            afterSale.setBeApplied(true);
            //设置售后记录是否删除
            afterSale.setBeDeleted(false);
            //设置申请时间
            afterSale.setApplyTime(LocalDateTime.now());
            //设置售后记录创建时间，标准组回复与上面的一样
            afterSale.setGmtCreate(LocalDateTime.now());
            //设置最近一次修改时间
            afterSale.setGmtModified(LocalDateTime.now());
            //设置结束时间应该不在这里，当管理员审核后才设置
            //返回创建后的售后订单
            return afterSaleDao.insertAfterSale(afterSale);
        }
        else {       //订单超出7天，无法申请
            return null;
        }
    }

    @Override
    public int deleteAfterSale(Integer id) {
        return afterSaleDao.deleteAfterSale(id);
    }
}
