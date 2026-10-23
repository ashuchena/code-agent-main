package com.campus.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.agent.entity.Volunteer;
import com.campus.agent.entity.VolunteerSignUp;
import com.campus.agent.mapper.VolunteerMapper;
import com.campus.agent.mapper.VolunteerSignUpMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VolunteerService {

    @Autowired
    private VolunteerMapper volunteerMapper;

    @Autowired
    private VolunteerSignUpMapper signUpMapper;

    public boolean addActivity(Volunteer activity) {
        validateActivity(activity);
        activity.setStatus(0);
        activity.setCurrentParticipants(0);
        activity.setCreateTime(LocalDateTime.now());
        activity.setApprovalStatus(0);
        return volunteerMapper.insert(activity) > 0;
    }

    public List<Volunteer> getAvailableList() {
        LambdaQueryWrapper<Volunteer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Volunteer::getStatus, 0)
               .eq(Volunteer::getApprovalStatus, 1)
               .orderByDesc(Volunteer::getCreateTime);
        return volunteerMapper.selectList(wrapper);
    }

    public List<Volunteer> getAllList() {
        LambdaQueryWrapper<Volunteer> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Volunteer::getCreateTime);
        return volunteerMapper.selectList(wrapper);
    }

    public boolean signUp(Long activityId, Long userId, String userName) {
        LambdaQueryWrapper<VolunteerSignUp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VolunteerSignUp::getActivityId, activityId)
               .eq(VolunteerSignUp::getUserId, userId);
        if (signUpMapper.selectCount(wrapper) > 0) {
            return false;
        }

        Volunteer activity = volunteerMapper.selectById(activityId);
        if (activity == null || activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            return false;
        }

        VolunteerSignUp signUp = new VolunteerSignUp();
        signUp.setActivityId(activityId);
        signUp.setUserId(userId);
        signUp.setUserName(userName);
        signUp.setSignUpTime(LocalDateTime.now());
        signUpMapper.insert(signUp);

        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        volunteerMapper.updateById(activity);

        return true;
    }

    public List<VolunteerSignUp> getMySignUps(Long userId) {
        LambdaQueryWrapper<VolunteerSignUp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VolunteerSignUp::getUserId, userId).orderByDesc(VolunteerSignUp::getSignUpTime);
        return signUpMapper.selectList(wrapper);
    }

    public boolean cancelSignUp(Long activityId, Long userId) {
        LambdaQueryWrapper<VolunteerSignUp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VolunteerSignUp::getActivityId, activityId)
               .eq(VolunteerSignUp::getUserId, userId);
        int deleted = signUpMapper.delete(wrapper);

        if (deleted > 0) {
            Volunteer activity = volunteerMapper.selectById(activityId);
            if (activity != null && activity.getCurrentParticipants() > 0) {
                activity.setCurrentParticipants(activity.getCurrentParticipants() - 1);
                volunteerMapper.updateById(activity);
            }
            return true;
        }
        return false;
    }

    public boolean delete(Long id) {
        return volunteerMapper.deleteById(id) > 0;
    }

    private void validateActivity(Volunteer activity) {
        if (activity == null) {
            throw new IllegalArgumentException("活动数据不能为空");
        }
        if (isBlank(activity.getTitle()) || isBlank(activity.getDescription()) || isBlank(activity.getLocation())) {
            throw new IllegalArgumentException("请完整填写活动标题、描述和地点");
        }
        LocalDate startDate = activity.getStartDate();
        if (startDate == null) {
            throw new IllegalArgumentException("请选择有效的活动日期");
        }
        if (isBlank(activity.getStartTime()) || isBlank(activity.getEndTime())) {
            throw new IllegalArgumentException("请选择完整的开始和结束时间");
        }
        if (activity.getMaxParticipants() == null || activity.getMaxParticipants() <= 0) {
            throw new IllegalArgumentException("招募人数必须大于 0");
        }
        if (activity.getHours() == null || activity.getHours().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("志愿时长必须大于 0");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
