package cn.ittiger.im;

import java.util.ArrayList;
import java.util.List;

import cn.ittiger.im.bean.MemberBean;
import cn.ittiger.im.bean.PageBean;
import cn.ittiger.im.bean.UserInfo;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * <pre>
 *      author  :yutao
 *      time    :2018/03/28
 *      desc    :
 * </pre>
 */

public interface Api {

    //查询列表
    @POST("login")
    Observable<WrapData<UserInfo>> getConsumeList(@Query("key") String key,
                                                  @Query("userName") String userName,
                                                  @Query("password") String password);

    @POST("listByPage")
    Observable<ArrayList<PageBean>> getPage(@Query("pages")int page, @Query("rows") int rows);


    @GET("merberships/list")
    Observable<List<MemberBean>> getMembers();
}
