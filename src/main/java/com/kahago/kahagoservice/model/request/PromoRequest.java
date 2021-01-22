package com.kahago.kahagoservice.model.request;

import lombok.Data;

/**
 * @author Ibnu Wasis
 */
@Data
public class PromoRequest {
	private Integer seqid;
	private Integer step;
	private String promoName;
	private String description;
	private Boolean showDashboard;
	private ImageRequest imagePath;
	private ImageRequest imageBack;
	private ImageRequest imageBlast;
}
