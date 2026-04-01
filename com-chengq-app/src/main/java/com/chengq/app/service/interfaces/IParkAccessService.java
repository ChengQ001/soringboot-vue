package com.chengq.app.service.interfaces;

import com.chengq.api.entity.Park;
import com.chengq.api.entity.User;
import java.util.List;

public interface IParkAccessService {

    List<Park> listAccessibleParks(Long userId);

    void resolveAndSetCurrentPark(User user, Long requestedParkId);
}
