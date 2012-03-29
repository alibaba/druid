package com.alibaba.druid.spring;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class UserService implements IUserService {

    private IUserDao         dao;
    private ISequenceService sequenceService;

    @Transactional(propagation = Propagation.REQUIRED)
    public void addUser(User user) {
        user.setId(sequenceService.nextValue());
        dao.addUser(user);
    }

    public IUserDao getDao() {
        return dao;
    }

    public void setDao(IUserDao dao) {
        this.dao = dao;
    }

    public ISequenceService getSequenceService() {
        return sequenceService;
    }

    public void setSequenceService(ISequenceService sequenceService) {
        this.sequenceService = sequenceService;
    }

}
