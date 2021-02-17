package com.meidianyi.shop.service.pojo.shop.market.message;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

/**
 * 推送统计VO
 * @author 卢光耀
 * @date 2019-09-16 14:17
 *
*/
@Data
public class MessageStatisticsVo {
    private Integer sendNumber;

    private Integer sendSuccessNumber;

    private Integer visitNumber;

    private Double visitPercentage;

    private List<StatisticsByDay> allStatistics;
    public class StatisticsByDay{

        private LocalDate date;

        private Integer sendNumber;

        private Integer sendSuccessNumber;

        private Integer visitNumber;

        private Double visitPercentage;

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public Integer getSendNumber() {
            return sendNumber;
        }

        public void setSendNumber(Integer sendNumber) {
            this.sendNumber = sendNumber;
        }

        public Integer getSendSuccessNumber() {
            return sendSuccessNumber;
        }

        public void setSendSuccessNumber(Integer sendSuccessNumber) {
            this.sendSuccessNumber = sendSuccessNumber;
        }

        public Integer getVisitNumber() {
            return visitNumber;
        }

        public void setVisitNumber(Integer visitNumber) {
            this.visitNumber = visitNumber;
        }

        public Double getVisitPercentage() {
            return visitPercentage;
        }

        public void setVisitPercentage(Double visitPercentage) {
            this.visitPercentage = visitPercentage;
        }
    }
}
