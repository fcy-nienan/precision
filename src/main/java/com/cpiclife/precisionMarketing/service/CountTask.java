package com.cpiclife.precisionMarketing.service;
import com.cpiclife.precisionMarketing.dao.*;
import com.cpiclife.precisionMarketing.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;
@Component
public class CountTask {
	@Value("${precision.url}")
	private String precisionUrl;

	public String getPrecisionUrl() {
		return precisionUrl;
	}

	public void setPrecisionUrl(String precisionUrl) {
		this.precisionUrl = precisionUrl;
	}
	 
    
}
