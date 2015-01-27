package com.xt.core.validation;


/**
 * 本类封装了所有校验相关的信息。
 * @author albert
 */
public class ValidateMessage implements Comparable {

    /**
     * 消息内容
     */
    private String message;

    /**
     * 用于对错误提供友善的提示信息。
     */
    private String tip;

    /**
     * 错误的级别。
     */
    private Level level = Level.ERROR;

    public ValidateMessage() {
    }

    public ValidateMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ValidateMessage other = (ValidateMessage) obj;
        if (this.message != other.message && (this.message == null || !this.message.equals(other.message))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.message != null ? this.message.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(Object o) {
        if (message != null) {
            return message.compareTo(o == null ? "" : o.toString());
        }
        return -1;
    }
}
