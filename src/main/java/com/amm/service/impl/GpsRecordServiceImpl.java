package com.amm.service.impl;

import com.amm.entity.GpsRecordEntity;
import com.amm.entity.MachTerminalEntity;
import com.amm.repository.GpsRecordRepository;
import com.amm.repository.RefMachTerminalRepository;
import com.amm.service.GpsRecordService;
import com.amm.service.MachTerminalService;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by csw on 2016/8/2 11:17.
 * Explain:
 */
@Component("gpsRecordService")
@Scope("prototype")
public class GpsRecordServiceImpl extends BaseService implements GpsRecordService{

    @Autowired
    private GpsRecordRepository gpsRecordRepository;

    @Autowired
    private RefMachTerminalRepository refMachTerminalRepository;

    @Autowired
    private MachTerminalService machTerminalService;

    public List<GpsRecordEntity> findAllGpsRecord() {

        return (List<GpsRecordEntity>) gpsRecordRepository.findAll();
    }

    public List<GpsRecordEntity> findByRefMachTerminalID(Integer id) {

        Validate.notNull(id, "The id must not be null, find failure.");

        List<GpsRecordEntity> gpsRecordEntityList = gpsRecordRepository.findByRefMachTerminalId(id);

        return gpsRecordEntityList;
    }

    @Transactional
    public GpsRecordEntity create(GpsRecordEntity gpsRecordEntity) {

        Validate.notNull(gpsRecordEntity, "The gpsRecordEntity must not be null, create failure.");
        Validate.notNull(gpsRecordEntity.getTerminalCode(), "The terminalCode of gpsRecordEntity must not be null, create failure.");

        MachTerminalEntity machTerminalEntity = machTerminalService.findByTerminalCode(gpsRecordEntity.getTerminalCode());

        Integer refMachTerminalId = machTerminalEntity != null ? machTerminalEntity.getRefMachTerminalId() : null;

        gpsRecordEntity.setRefMachTerminalId(refMachTerminalId);
        gpsRecordEntity.setLocalTime(new Date());

        GpsRecordEntity created = gpsRecordRepository.save(gpsRecordEntity);

        return created;
    }

    public GpsRecordEntity findOne(Integer id) {
        Validate.notNull(id, "The id must not be null, find failure.");
        return gpsRecordRepository.findOne(id);
    }

    public List<GpsRecordEntity> findGpsRecordByTimeScope(Date startTime, Date endTime) {

        Validate.notNull(startTime, "The startTime must not be null, find failure.");
        Validate.notNull(endTime, "The endTime must not be null, find failure.");

        List<GpsRecordEntity> gpsRecordEntityList = gpsRecordRepository.findByGpsTimeAfterAndGpsTimeBeforeAndRefMachTerminalIdIsNotNull(startTime, endTime);;

        return gpsRecordEntityList;
    }

    public List<GpsRecordEntity> findByRefMachTerminalIDAndTimeScope(Integer id, Date startTime, Date endTime) {

        Validate.notNull(id, "The id must not be null, find failure.");
        Validate.notNull(startTime, "The startTime must not be null, find failure.");
        Validate.notNull(endTime, "The endTime must not be null, find failure.");

        return gpsRecordRepository.findByRefMachTerminalIdAndGpsTimeBetweenAndLngFixedIsNotNull(id, startTime, endTime);
    }

    public List<GpsRecordEntity> findByLatFixedIsNullOrderbyGpsTimeAsc() {
        return gpsRecordRepository.findByLatFixedOrderByGpsTimeAsc(null);
    }

    public GpsRecordEntity updateGpsRecord(GpsRecordEntity gpsRecord) {

        Validate.notNull(gpsRecord.getId(), "The id must not be null, find failure.");

        GpsRecordEntity saved = gpsRecordRepository.findOne(gpsRecord.getId());

        //仅改变修正后的经纬度,其他不做变动
        saved.setLngFixed(gpsRecord.getLngFixed());
        saved.setLatFixed(gpsRecord.getLatFixed());

        saved = gpsRecordRepository.save(saved);

        return null;
    }

    public List<GpsRecordEntity> getFirst() {
        return gpsRecordRepository.getFirst();
    }

    public List<GpsRecordEntity> getFinishingData() {
        return gpsRecordRepository.getFinishingData();
    }

    @Transactional
    public void updateState(String state) {
        gpsRecordRepository.updateState(state);
    }


}
