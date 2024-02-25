package com.pandaer.pan.server.modules.share.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户分享表
 * @TableName m_pan_share
 */
@TableName(value ="m_pan_share")
@Data
public class MPanShare implements Serializable {
    /**
     * 分享id
     */
    @TableId(value = "share_id")
    private Long shareId;

    /**
     * 分享名称
     */
    @TableField(value = "share_name")
    private String shareName;

    /**
     * 分享类型（0 有提取码）
     */
    @TableField(value = "share_type")
    private Integer shareType;

    /**
     * 分享类型（0 永久有效；1 7天有效；2 30天有效）
     */
    @TableField(value = "share_day_type")
    private Integer shareDayType;

    /**
     * 分享有效天数（永久有效为0）
     */
    @TableField(value = "share_day")
    private Integer shareDay;

    /**
     * 分享结束时间
     */
    @TableField(value = "share_end_time")
    private Date shareEndTime;

    /**
     * 分享链接地址
     */
    @TableField(value = "share_url")
    private String shareUrl;

    /**
     * 分享提取码
     */
    @TableField(value = "share_code")
    private String shareCode;

    /**
     * 分享状态（0 正常；1 有文件被删除）
     */
    @TableField(value = "share_status")
    private Integer shareStatus;

    /**
     * 分享创建人
     */
    @TableField(value = "create_user")
    private Long createUser;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        MPanShare other = (MPanShare) that;
        return (this.getShareId() == null ? other.getShareId() == null : this.getShareId().equals(other.getShareId()))
            && (this.getShareName() == null ? other.getShareName() == null : this.getShareName().equals(other.getShareName()))
            && (this.getShareType() == null ? other.getShareType() == null : this.getShareType().equals(other.getShareType()))
            && (this.getShareDayType() == null ? other.getShareDayType() == null : this.getShareDayType().equals(other.getShareDayType()))
            && (this.getShareDay() == null ? other.getShareDay() == null : this.getShareDay().equals(other.getShareDay()))
            && (this.getShareEndTime() == null ? other.getShareEndTime() == null : this.getShareEndTime().equals(other.getShareEndTime()))
            && (this.getShareUrl() == null ? other.getShareUrl() == null : this.getShareUrl().equals(other.getShareUrl()))
            && (this.getShareCode() == null ? other.getShareCode() == null : this.getShareCode().equals(other.getShareCode()))
            && (this.getShareStatus() == null ? other.getShareStatus() == null : this.getShareStatus().equals(other.getShareStatus()))
            && (this.getCreateUser() == null ? other.getCreateUser() == null : this.getCreateUser().equals(other.getCreateUser()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getShareId() == null) ? 0 : getShareId().hashCode());
        result = prime * result + ((getShareName() == null) ? 0 : getShareName().hashCode());
        result = prime * result + ((getShareType() == null) ? 0 : getShareType().hashCode());
        result = prime * result + ((getShareDayType() == null) ? 0 : getShareDayType().hashCode());
        result = prime * result + ((getShareDay() == null) ? 0 : getShareDay().hashCode());
        result = prime * result + ((getShareEndTime() == null) ? 0 : getShareEndTime().hashCode());
        result = prime * result + ((getShareUrl() == null) ? 0 : getShareUrl().hashCode());
        result = prime * result + ((getShareCode() == null) ? 0 : getShareCode().hashCode());
        result = prime * result + ((getShareStatus() == null) ? 0 : getShareStatus().hashCode());
        result = prime * result + ((getCreateUser() == null) ? 0 : getCreateUser().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", shareId=").append(shareId);
        sb.append(", shareName=").append(shareName);
        sb.append(", shareType=").append(shareType);
        sb.append(", shareDayType=").append(shareDayType);
        sb.append(", shareDay=").append(shareDay);
        sb.append(", shareEndTime=").append(shareEndTime);
        sb.append(", shareUrl=").append(shareUrl);
        sb.append(", shareCode=").append(shareCode);
        sb.append(", shareStatus=").append(shareStatus);
        sb.append(", createUser=").append(createUser);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}