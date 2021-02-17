package com.meidianyi.shop.service.pojo.saas.schedule.rabbit;

/**
 * @author luguangyao
 */
public class RabbitDataConstant {

    public enum State{
        RUNNING("running","运行中");

        State(String state,String name){
            this.state = state;
            this.name = name;
        }
        private String state;

        private String name;

        public String getState() {
            return state;
        }

        public String getName() {
            return name;
        }

        public static String getNameByState(String state){
            for( State s: values() ){
                if( s.getState().equals(state) ){
                    return s.getName();
                }
            }
            return null;
        }
    }
}
