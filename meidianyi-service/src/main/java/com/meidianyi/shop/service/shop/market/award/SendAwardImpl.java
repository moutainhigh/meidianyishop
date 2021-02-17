package com.meidianyi.shop.service.shop.market.award;

/**
 * The type Send award.
 *
 * @author liufei
 * @date 1 /14/20
 */
public class SendAwardImpl implements SendAward {
    private AwardParam awardParam;

    private void setAwardParam(AwardParam awardParam) {
        this.awardParam = awardParam;
    }

    /**
     * Builder builder.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * The type Builder.
     */
    public static class Builder {
        private Builder() {
        }

        private AwardParam awardParam;

        /**
         * Sets award param.
         *
         * @param awardParam the award param
         * @return the award param
         */
        public Builder setAwardParam(AwardParam awardParam) {
            this.awardParam = awardParam;
            return this;
        }

        /**
         * Build send award.
         *
         * @return the send award
         */
        public SendAwardImpl build() {
            SendAwardImpl target = new SendAwardImpl();
            target.setAwardParam(this.awardParam);
            return target;
        }
    }

    @Override
    public void send(Award award) {
        award.sendAward(awardParam);
    }
}
