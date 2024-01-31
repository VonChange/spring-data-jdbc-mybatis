package com.vonchange.nine.demo.domain;

public  enum EnumDelete {
       deleted(1,"已删除"),unDeleted(0,"未删除");

        private Integer value;
        private String desc;
        EnumDelete(Integer value, String desc){
            this.value=value;
            this.desc=desc;
        }

        public static EnumDelete getValue(Integer value) {
            for (EnumDelete c : EnumDelete.values()) {
                if (c.getValue().equals(value)) {
                    return c;
                }
            }
            return null;
        }
        public Integer getValue() {
            return value;
        }

        public EnumDelete setValue(int value) {
            this.value = value;
            return this;

        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }