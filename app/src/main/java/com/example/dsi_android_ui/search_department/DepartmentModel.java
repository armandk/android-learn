package com.example.dsi_android_ui.search_department;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

import lombok.Data;

@Data
public class DepartmentModel {


        @SerializedName("result")
        @Expose
        private HashMap<String, Integer> result;

        public HashMap<String, Integer> getResult() {
                return result;
        }

}
