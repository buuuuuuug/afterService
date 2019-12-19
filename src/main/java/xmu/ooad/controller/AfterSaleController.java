package xmu.ooad.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xmu.ooad.domain.AfterSale;
import xmu.ooad.service.AfterSaleService;
import xmu.ooad.util.ResponseUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 售后API
 * @Author zjy
 * @create 2019/12/5 17:38
 */

@RestController
@RequestMapping("")
public class AfterSaleController {

    @Autowired
    private AfterSaleService afterSaleService;

    /**
     * 管理员查询某一个用户所有售后服务列表
     *
     * @return
     */
    @GetMapping("/admin/afterSaleServices")
    public Object adminFindAfterSalesServiceList(@RequestParam Integer userId,
                                                 @RequestParam Integer limit,
                                                 @RequestParam Integer page) {
        //用户服务网关已经拦截了非法请求，因此这里不需要再次验证
        List<AfterSale> afterSales = afterSaleService.findAfterSales(userId);
        if(afterSales!=null){
            return ResponseUtil.okList(afterSales);
        }else {
            return ResponseUtil.ok(null);
        }
    }

    /**
     * 管理员通过id查询某一个售后服务具体信息
     * @param id
     * @return
     */
    @GetMapping("/admin/afterSaleServices/{id}")
    public Object adminFindAfterSaleServiceById(@PathVariable Integer id) {
        //用户服务网关已经拦截了非法请求，因此这里不需要再次验证是否为管理员
        //id不为空
        if(id!=null){
            //查询结果
            AfterSale afterSale = afterSaleService.findAfterSaleById(id);
            //查询结果不为空，返回成功状态码及数据
            if(afterSale!=null) {
                return ResponseUtil.ok(afterSale);
            }else{   //查询结果为空，返回参数不对
                System.out.println("查询结果为空!");
                return ResponseUtil.badArgument();
            }
        }else{              //id为空，返回参数不对？
            return ResponseUtil.badArgument();
        }
    }

    /**
     * 管理员根据id修改售后服务的信息（有requestBody版本，application/json）
     * @param id
     * @return
     */
    @PutMapping("/admin/afterSaleServices/{id}")
    public Object adminUpdateAfterSaleService(@PathVariable Integer id,
                                              @RequestBody AfterSale afterSale) {
        //id和afterSale都不为空，才能继续进行操作
        if(id!=null && afterSale!=null) {
            afterSale.setId(id);
            //afterSale1 是修改后的售后记录
            AfterSale afterSale1 = afterSaleService.adminUpdateAfterSale(afterSale);
            if (afterSale1 != null) {
                return ResponseUtil.ok(afterSale1);
            } else {
                //修改后的售后记录为空，返回更新数据失败
                return ResponseUtil.updatedDataFailed();
            }
        }else{              //id或afterSale为空，返回更新数据失败
            return ResponseUtil.updatedDataFailed();
        }
    }

    /**
     * 用户查询自己的售后服务列表（不返回已删除的售后记录），需要userId
     * @return
     */
    @GetMapping("/afterSaleService")
    public Object userFindAfterSaleServiceList(HttpServletRequest request,
                                               @RequestParam Integer limit,
                                               @RequestParam Integer page,
                                               @RequestParam Integer userId) {
        //集成用户服务，获取userId
        //Integer userId=getUserId(request);
        if(userId!=null) {
            List<AfterSale> afterSales = afterSaleService.findAfterSalesByUserId(userId);
            if (afterSales != null) {
                return ResponseUtil.okList(afterSales);
            } else {
                return ResponseUtil.ok(null);
            }
        }else{
            return ResponseUtil.badArgument();  //userId为空，返回参数不对
        }
    }

    /**
     * 用户查询某一个售后服务的具体信息，当这个售后被删除后，返回空列表
     * @param id
     * @return
     */
    @GetMapping("/afterSaleService/{id}")
    public Object userFindAfterSaleService(@PathVariable Integer id,
                                           @RequestParam Integer userId) {
        //userId由用户服务提供的方法获得  httpServletRequest参数
        AfterSale afterSale = afterSaleService.findAfterSaleById(id);
        if(userId.equals(afterSale.getUserId())){       //访问的是自己的售后服务
            if(!afterSale.getBeDeleted()){        //没有被删除
                return ResponseUtil.ok(afterSale);
            }else{                                   //删除了
                return ResponseUtil.ok(null);
            }
        }else {
            return ResponseUtil.unauthz();       //无操作权限
        }
    }

    /**
     * 用户申请售后服务，需要提供
     * goodsType   只能是1，2，3，4
     * applyReason
     * type     只能是0，1
     * number
     * orderItemId
     * userId    这里应该不是写进afterSale中的，由用户服务提供的方法获得
     * @param userId
     * @param afterSale
     * @return
     */
    @PostMapping("/afterSaleService")
    public Object userApplyAfterSaleService(@RequestParam Integer userId,
                                            @RequestBody AfterSale afterSale) {
        //userId由用户服务提供的方法获得
        if(userId!=null && afterSale!=null) {
            int type = afterSale.getType();
            if(type<0 || type>1){
                return ResponseUtil.badArgumentValue();       //参数值不对
            }
            int goodsType = afterSale.getGoodsType();
            if(goodsType<1 || goodsType >4){
                return ResponseUtil.badArgumentValue();       //参数值不对
            }
            //设置userId
            afterSale.setUserId(userId);
            AfterSale afterSale1 = afterSaleService.insertAfterSale(afterSale);
            if(afterSale1!=null){
                return ResponseUtil.ok(afterSale1);    //返回申请好的售后记录
            }else{
                return ResponseUtil.unsupport();     //订单超过七天，业务不支持
            }
        }else{
            return ResponseUtil.badArgument();     //参数不对
        }
    }

    /**
     * 用户修改售后服务的信息，在管理员审核之前，可以修改：
     * goodsType     商品类别
     * applyReason    申请理由
     * type          售后类型
     * number        申请数量
     * beApplied     是否取消
     * 在管理员审核之后，用户只可以修改是否取消售后，即：
     * beApplied=false
     * @param id
     * @param afterSale
     * @return
     */
    @PutMapping("/afterSaleService/{id}")
    public Object userUpdateAfterSaleService(@PathVariable Integer id,
                                             @RequestBody AfterSale afterSale) {
        //应该也要验证用户身份，通过token获取userId
        if(id!=null && afterSale!=null) {
            afterSale.setId(id);
            //在此处将userId写进对象afterSale中
            AfterSale afterSale1 = afterSaleService.updateAfterSale(afterSale);
            //afterSale1 是修改后的售后记录
            if(afterSale1!=null) {
                //比较afterSale 的userId 和afterSale1 的userId ，相同返回修改后的售后记录
                if(afterSale.getUserId()==afterSale1.getUserId()) {
                    return ResponseUtil.ok(afterSale1);
                }else{
                    //不同返回无操作权限
                    return ResponseUtil.unauthz();
                }
            }else{
                return ResponseUtil.updatedDataFailed(); //更新数据失败
            }
        }else{
            return ResponseUtil.updatedDataFailed();      //更新数据失败
        }
    }

    /**
     * 用户删除某一个售后服务
     * 只有管理员审核之后才能删除？
     * @param id
     * @return
     */
    @DeleteMapping("/afterSaleService/{id}")
    public Object userDeleteAfterSaleService(@PathVariable Integer id) {
        //是不是也要解析userId?
        if(id!=null) {
            return afterSaleService.deleteAfterSale(id);
        }else{
            return ResponseUtil.badArgument();
        }
    }
}

