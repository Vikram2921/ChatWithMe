package com.nobodyknows.chatwithme.Activities.Dashboard.Interfaces;

import com.NobodyKnows.chatlayoutview.Model.User;
import com.nobodyknows.chatwithme.DTOS.VaccineSessions;

import org.json.JSONObject;

import java.util.List;

public interface VaccineSelectListener {
    public void onSchedule(VaccineSessions vaccineSessions,String slot);
}
