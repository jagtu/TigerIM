package cn.ittiger.im.bean;

import java.util.List;

/**
 * <p></p>
 * <p>
 * Created by zhangli on 2018/5/11 12:02.<br/>
 */
public class UserInfo {


    /**
     * userName : zs1001
     * nickName : willow1
     * floor : 1
     * superior : {"userName":"pt100","nickName":"pt100","floor":0}
     * juniorList : [{"userName":"zhishu1","nickName":"","floor":2},{"userName":"zhishu2","nickName":"","floor":2},{"userName":"zhishu3","nickName":"","floor":2},{"userName":"zhishu123","nickName":"","floor":2},{"userName":"52452","nickName":"","floor":2}]
     */

    private String userName;
    private String nickName;
    private int floor;
    private SuperiorBean superior;
    private List<JuniorListBean> juniorList;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public SuperiorBean getSuperior() {
        return superior;
    }

    public void setSuperior(SuperiorBean superior) {
        this.superior = superior;
    }

    public List<JuniorListBean> getJuniorList() {
        return juniorList;
    }

    public void setJuniorList(List<JuniorListBean> juniorList) {
        this.juniorList = juniorList;
    }

    public static class SuperiorBean {
        /**
         * userName : pt100
         * nickName : pt100
         * floor : 0
         */

        private String userName;
        private String nickName;
        private int floor;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public int getFloor() {
            return floor;
        }

        public void setFloor(int floor) {
            this.floor = floor;
        }
    }

    public static class JuniorListBean {
        /**
         * userName : zhishu1
         * nickName :
         * floor : 2
         */

        private String userName;
        private String nickName;
        private int floor;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public int getFloor() {
            return floor;
        }

        public void setFloor(int floor) {
            this.floor = floor;
        }
    }
}
