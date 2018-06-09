package cn.ittiger.im.util;

import cn.ittiger.im.R;
import cn.ittiger.im.constant.EmotionType;

import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;

/**
 * 表情数据帮助类
 *
 * @author: laohu on 2017/2/6
 * @site: http://ittiger.cn
 */
public class EmotionDataHelper {
    /**
     * key -- 表情名称
     * value -- 表情图片resId
     */
    private static ArrayMap<String, Integer> sEmotionClassicMap;
    /**
     * 表情页底部的表情类型Tab数据
     */
    private static List<EmotionType> sEmotionTabList;

    static {
        sEmotionClassicMap = new ArrayMap<>();

        sEmotionTabList = new ArrayList<>();
        sEmotionTabList.add(EmotionType.EMOTION_TYPE_CLASSIC);
        sEmotionTabList.add(EmotionType.EMOTION_TYPE_MORE);
        sEmotionClassicMap.put("[微笑]", R.mipmap.d_weixiao);
        sEmotionClassicMap.put("[撇嘴]", R.mipmap.d_pianzui);
        sEmotionClassicMap.put("[色]", R.mipmap.d_se);
        sEmotionClassicMap.put("[发呆]", R.mipmap.d_fadai);
        sEmotionClassicMap.put("[得意]", R.mipmap.d_deiyi);
        sEmotionClassicMap.put("[流泪]", R.mipmap.d_liulei);
        sEmotionClassicMap.put("[害羞]", R.mipmap.d_haixiu);
        sEmotionClassicMap.put("[闭嘴]", R.mipmap.d_bizui);
        sEmotionClassicMap.put("[睡]", R.mipmap.d_shui);
        sEmotionClassicMap.put("[大哭]", R.mipmap.d_daku);
        sEmotionClassicMap.put("[尴尬]", R.mipmap.d_ganga);
        sEmotionClassicMap.put("[发怒]", R.mipmap.d_fanu);
        sEmotionClassicMap.put("[调皮]", R.mipmap.d_tiaopi);
        sEmotionClassicMap.put("[呲牙]", R.mipmap.d_ciya);
        sEmotionClassicMap.put("[惊讶]", R.mipmap.d_jingya);
        sEmotionClassicMap.put("[难过]", R.mipmap.d_nanguo);
        sEmotionClassicMap.put("[酷]", R.mipmap.d_ku);
        sEmotionClassicMap.put("[冷汗]", R.mipmap.d_linghan);
        sEmotionClassicMap.put("[抓狂]", R.mipmap.d_zhuakuang);
        sEmotionClassicMap.put("[吐]", R.mipmap.d_tu);
        sEmotionClassicMap.put("[偷笑]", R.mipmap.d_touxiao);
        sEmotionClassicMap.put("[可爱]", R.mipmap.d_keai);
        sEmotionClassicMap.put("[白眼]", R.mipmap.d_baiyan);
        sEmotionClassicMap.put("[傲慢]", R.mipmap.d_aoman);
        sEmotionClassicMap.put("[饥饿]", R.mipmap.d_jie);
        sEmotionClassicMap.put("[困]", R.mipmap.d_kun);
        sEmotionClassicMap.put("[惊恐]", R.mipmap.d_jingkong);
        sEmotionClassicMap.put("[流汗]", R.mipmap.d_liuhan);
        sEmotionClassicMap.put("[憨笑]", R.mipmap.d_hanxiao);
        sEmotionClassicMap.put("[大兵]", R.mipmap.d_dabing);
        sEmotionClassicMap.put("[奋斗]", R.mipmap.d_fendou);
        sEmotionClassicMap.put("[咒骂]", R.mipmap.d_zhouma);
        sEmotionClassicMap.put("[疑问]", R.mipmap.d_yiwen);
        sEmotionClassicMap.put("[嘘]", R.mipmap.d_xu);
        sEmotionClassicMap.put("[晕]", R.mipmap.d_yun);
        sEmotionClassicMap.put("[折磨]", R.mipmap.d_zhemo);
        sEmotionClassicMap.put("[衰]", R.mipmap.d_shuai);
        sEmotionClassicMap.put("[骷髅]", R.mipmap.d_kulou);
        sEmotionClassicMap.put("[敲打]", R.mipmap.d_qiaoda);
        sEmotionClassicMap.put("[再见]", R.mipmap.d_zaijian);
        sEmotionClassicMap.put("[擦汗]", R.mipmap.d_cahan);
        sEmotionClassicMap.put("[抠鼻]", R.mipmap.d_koubi);
        sEmotionClassicMap.put("[鼓掌]", R.mipmap.d_guzhang);
        sEmotionClassicMap.put("[糗大了]", R.mipmap.d_choudale);
        sEmotionClassicMap.put("[左哼哼]", R.mipmap.d_zuohengheng);
        sEmotionClassicMap.put("[右哼哼]", R.mipmap.d_youhengheng);
        sEmotionClassicMap.put("[哈欠]", R.mipmap.d_haqie);
        sEmotionClassicMap.put("[鄙视]", R.mipmap.d_bishi);
        sEmotionClassicMap.put("[委屈]", R.mipmap.d_weiqu);
        sEmotionClassicMap.put("[快哭了]", R.mipmap.d_kuaikule);
        sEmotionClassicMap.put("[阴险]", R.mipmap.d_yinxian);
        sEmotionClassicMap.put("[亲亲]", R.mipmap.d_qinqin);
        sEmotionClassicMap.put("[吓]", R.mipmap.d_xia);
        sEmotionClassicMap.put("[可怜]", R.mipmap.d_kelian);
        sEmotionClassicMap.put("[菜刀]", R.mipmap.d_caidao);
        sEmotionClassicMap.put("[西瓜]", R.mipmap.d_xigua);
        sEmotionClassicMap.put("[啤酒]", R.mipmap.d_pijiu);
        sEmotionClassicMap.put("[篮球]", R.mipmap.d_lanqiu);
        sEmotionClassicMap.put("[乒乓]", R.mipmap.d_pingpang);
        sEmotionClassicMap.put("[咖啡]", R.mipmap.d_kafei);
        sEmotionClassicMap.put("[饭]", R.mipmap.d_fan);
        sEmotionClassicMap.put("[猪头]", R.mipmap.d_zhutou);
        sEmotionClassicMap.put("[玫瑰]", R.mipmap.d_meigui);
        sEmotionClassicMap.put("[凋谢]", R.mipmap.d_diaoxie);
        sEmotionClassicMap.put("[示爱]", R.mipmap.d_shiai);
        sEmotionClassicMap.put("[爱心]", R.mipmap.d_aixin);
        sEmotionClassicMap.put("[心碎]", R.mipmap.d_xinsui);
        sEmotionClassicMap.put("[蛋糕]", R.mipmap.d_dangao);
        sEmotionClassicMap.put("[闪电]", R.mipmap.d_shandian);
        sEmotionClassicMap.put("[炸弹]", R.mipmap.d_zhadan);
        sEmotionClassicMap.put("[刀]", R.mipmap.d_dao);
        sEmotionClassicMap.put("[足球]", R.mipmap.d_zuqiu);
        sEmotionClassicMap.put("[瓢虫]", R.mipmap.d_piaochong);
        sEmotionClassicMap.put("[便便]", R.mipmap.d_bianbian);
        sEmotionClassicMap.put("[月亮]", R.mipmap.d_yueliang);
        sEmotionClassicMap.put("[太阳]", R.mipmap.d_taiyang);
        sEmotionClassicMap.put("[礼物]", R.mipmap.d_liwu);
        sEmotionClassicMap.put("[拥抱]", R.mipmap.d_yongbao);
        sEmotionClassicMap.put("[强]", R.mipmap.d_qiang);
        sEmotionClassicMap.put("[弱]", R.mipmap.d_ruo);
        sEmotionClassicMap.put("[握手]", R.mipmap.d_woshou);
        sEmotionClassicMap.put("[胜利]", R.mipmap.d_shengli);
        sEmotionClassicMap.put("[抱拳]", R.mipmap.d_baoquan);
        sEmotionClassicMap.put("[勾引]", R.mipmap.d_gouyin);
        sEmotionClassicMap.put("[拳头]", R.mipmap.d_quantou);
        sEmotionClassicMap.put("[差劲]", R.mipmap.d_chajin);
        sEmotionClassicMap.put("[爱你]", R.mipmap.d_aini);
        sEmotionClassicMap.put("[NO]", R.mipmap.d_no);
        sEmotionClassicMap.put("[OK]", R.mipmap.d_ok);
        sEmotionClassicMap.put("[爱情]", R.mipmap.d_aiqing);
        sEmotionClassicMap.put("[飞吻]", R.mipmap.d_feiwen);
        sEmotionClassicMap.put("[跳跳]", R.mipmap.d_tiaotiao);
        sEmotionClassicMap.put("[发抖]", R.mipmap.d_fadou);
        sEmotionClassicMap.put("[怄火]", R.mipmap.d_ouhuo);
        sEmotionClassicMap.put("[转圈]", R.mipmap.d_zhuanquan);
        sEmotionClassicMap.put("[磕头]", R.mipmap.d_ketou);
        sEmotionClassicMap.put("[回头]", R.mipmap.d_huitou);
        sEmotionClassicMap.put("[跳绳]", R.mipmap.d_tiaosheng);
        sEmotionClassicMap.put("[挥手]", R.mipmap.d_huishou);
        sEmotionClassicMap.put("[激动]", R.mipmap.d_jidong);
        sEmotionClassicMap.put("[街舞]", R.mipmap.d_jiewu);
        sEmotionClassicMap.put("[献吻]", R.mipmap.d_xianwen);
        sEmotionClassicMap.put("[左太极]", R.mipmap.d_zuotaiji);
        sEmotionClassicMap.put("[右太极]", R.mipmap.d_youtaiji);
        sEmotionClassicMap.put("[双喜]", R.mipmap.d_shuangxi);
        sEmotionClassicMap.put("[鞭炮]", R.mipmap.d_bianpao);
        sEmotionClassicMap.put("[灯笼]", R.mipmap.d_denglou);
        sEmotionClassicMap.put("[发财]", R.mipmap.d_facai);
        sEmotionClassicMap.put("[K歌]", R.mipmap.d_kge);
        sEmotionClassicMap.put("[购物]", R.mipmap.d_gouwu);
        sEmotionClassicMap.put("[邮件]", R.mipmap.d_youjian);
        sEmotionClassicMap.put("[帅]", R.mipmap.d_shuai);
        sEmotionClassicMap.put("[喝彩]", R.mipmap.d_hecai);
        sEmotionClassicMap.put("[祈祷]", R.mipmap.d_qidao);
        sEmotionClassicMap.put("[爆筋]", R.mipmap.d_baojin);
        sEmotionClassicMap.put("[棒棒糖]", R.mipmap.d_bangbangtang);
        sEmotionClassicMap.put("[喝奶]", R.mipmap.d_henai);
        sEmotionClassicMap.put("[下面]", R.mipmap.d_xiamian);
        sEmotionClassicMap.put("[香蕉]", R.mipmap.d_xiangjiao);
        sEmotionClassicMap.put("[飞机]", R.mipmap.d_feiji);
        sEmotionClassicMap.put("[开车]", R.mipmap.d_kaiche);
        sEmotionClassicMap.put("[左车头]", R.mipmap.d_zuochetou);
        sEmotionClassicMap.put("[车厢]", R.mipmap.d_chexiang);
        sEmotionClassicMap.put("[右车头]", R.mipmap.d_youchetou);
        sEmotionClassicMap.put("[多云]", R.mipmap.d_duoyun);
        sEmotionClassicMap.put("[下雨]", R.mipmap.d_xiayu);
        sEmotionClassicMap.put("[钞票]", R.mipmap.d_chaopiao);
        sEmotionClassicMap.put("[熊猫]", R.mipmap.d_xiongmao);
        sEmotionClassicMap.put("[灯泡]", R.mipmap.d_dengpao);
        sEmotionClassicMap.put("[风车]", R.mipmap.d_fengche);
        sEmotionClassicMap.put("[闹钟]", R.mipmap.d_naozhong);
        sEmotionClassicMap.put("[打伞]", R.mipmap.d_dasan);
        sEmotionClassicMap.put("[彩球]", R.mipmap.d_caiqiu);
        sEmotionClassicMap.put("[钻戒]", R.mipmap.d_zuanjie);
        sEmotionClassicMap.put("[沙发]", R.mipmap.d_shafa);
        sEmotionClassicMap.put("[纸巾]", R.mipmap.d_zhijin);
        sEmotionClassicMap.put("[药]", R.mipmap.d_yao);
        sEmotionClassicMap.put("[手枪]", R.mipmap.d_shouqiang);
        sEmotionClassicMap.put("[青蛙]", R.mipmap.d_qingwa);

//        sEmotionClassicMap.put("[呵呵]", R.drawable.d_hehe);
//        sEmotionClassicMap.put("[嘻嘻]", R.drawable.d_xixi);
//        sEmotionClassicMap.put("[哈哈]", R.drawable.d_haha);
//        sEmotionClassicMap.put("[爱你]", R.drawable.d_aini);
//        sEmotionClassicMap.put("[挖鼻屎]", R.drawable.d_wabishi);
//        sEmotionClassicMap.put("[吃惊]", R.drawable.d_chijing);
//        sEmotionClassicMap.put("[晕]", R.drawable.d_yun);
//        sEmotionClassicMap.put("[泪]", R.drawable.d_lei);
//        sEmotionClassicMap.put("[馋嘴]", R.drawable.d_chanzui);
//        sEmotionClassicMap.put("[抓狂]", R.drawable.d_zhuakuang);
//        sEmotionClassicMap.put("[哼]", R.drawable.d_heng);
//        sEmotionClassicMap.put("[可爱]", R.drawable.d_keai);
//        sEmotionClassicMap.put("[怒]", R.drawable.d_nu);
//        sEmotionClassicMap.put("[汗]", R.drawable.d_han);
//        sEmotionClassicMap.put("[害羞]", R.drawable.d_haixiu);
//        sEmotionClassicMap.put("[睡觉]", R.drawable.d_shuijiao);
//        sEmotionClassicMap.put("[钱]", R.drawable.d_qian);
//        sEmotionClassicMap.put("[偷笑]", R.drawable.d_touxiao);
//        sEmotionClassicMap.put("[笑cry]", R.drawable.d_xiaoku);
//        sEmotionClassicMap.put("[doge]", R.drawable.d_doge);
//        sEmotionClassicMap.put("[喵喵]", R.drawable.d_miao);
//        sEmotionClassicMap.put("[酷]", R.drawable.d_ku);
//        sEmotionClassicMap.put("[衰]", R.drawable.d_shuai);
//        sEmotionClassicMap.put("[闭嘴]", R.drawable.d_bizui);
//        sEmotionClassicMap.put("[鄙视]", R.drawable.d_bishi);
//        sEmotionClassicMap.put("[花心]", R.drawable.d_huaxin);
//        sEmotionClassicMap.put("[鼓掌]", R.drawable.d_guzhang);
//        sEmotionClassicMap.put("[悲伤]", R.drawable.d_beishang);
//        sEmotionClassicMap.put("[思考]", R.drawable.d_sikao);
//        sEmotionClassicMap.put("[生病]", R.drawable.d_shengbing);
//        sEmotionClassicMap.put("[亲亲]", R.drawable.d_qinqin);
//        sEmotionClassicMap.put("[怒骂]", R.drawable.d_numa);
//        sEmotionClassicMap.put("[太开心]", R.drawable.d_taikaixin);
//        sEmotionClassicMap.put("[懒得理你]", R.drawable.d_landelini);
//        sEmotionClassicMap.put("[右哼哼]", R.drawable.d_youhengheng);
//        sEmotionClassicMap.put("[左哼哼]", R.drawable.d_zuohengheng);
//        sEmotionClassicMap.put("[嘘]", R.drawable.d_xu);
//        sEmotionClassicMap.put("[委屈]", R.drawable.d_weiqu);
//        sEmotionClassicMap.put("[吐]", R.drawable.d_tu);
//        sEmotionClassicMap.put("[可怜]", R.drawable.d_kelian);
//        sEmotionClassicMap.put("[打哈气]", R.drawable.d_dahaqi);
//        sEmotionClassicMap.put("[挤眼]", R.drawable.d_jiyan);
//        sEmotionClassicMap.put("[失望]", R.drawable.d_shiwang);
//        sEmotionClassicMap.put("[顶]", R.drawable.d_ding);
//        sEmotionClassicMap.put("[疑问]", R.drawable.d_yiwen);
//        sEmotionClassicMap.put("[困]", R.drawable.d_kun);
//        sEmotionClassicMap.put("[感冒]", R.drawable.d_ganmao);
//        sEmotionClassicMap.put("[拜拜]", R.drawable.d_baibai);
//        sEmotionClassicMap.put("[黑线]", R.drawable.d_heixian);
//        sEmotionClassicMap.put("[阴险]", R.drawable.d_yinxian);
//        sEmotionClassicMap.put("[打脸]", R.drawable.d_dalian);
//        sEmotionClassicMap.put("[傻眼]", R.drawable.d_shayan);
//        sEmotionClassicMap.put("[猪头]", R.drawable.d_zhutou);
//        sEmotionClassicMap.put("[熊猫]", R.drawable.d_xiongmao);
//        sEmotionClassicMap.put("[兔子]", R.drawable.d_tuzi);
    }

    /**
     * 根据表情名称获取当前表情图标R值
     *
     * @param emotionName 表情名称
     * @return
     */
    public static int getEmotionForName(EmotionType emotionType, String emotionName) {

        ArrayMap<String, Integer> emotionMap = getEmotionsForType(emotionType);
        Integer emotionId = emotionMap.get(emotionName);
        return emotionId == null ? R.drawable.vector_default_image : emotionId.intValue();
    }

    public static List<EmotionType> getEmotionTabList() {

        return sEmotionTabList;
    }

    /**
     * 根据表情类型获取对应的表情列表
     *
     * @param emotionType
     * @return
     */
    public static ArrayMap<String, Integer> getEmotionsForType(EmotionType emotionType) {

        switch (emotionType) {
            case EMOTION_TYPE_CLASSIC:
                return sEmotionClassicMap;
            case EMOTION_TYPE_MORE:
                return new ArrayMap<>(0);
            default:
                return new ArrayMap<>(0);
        }
    }
}
