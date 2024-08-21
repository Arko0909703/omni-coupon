package com.project.coupon.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="unique_coupon")
@Getter
@Setter
public class UniqueCouponEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	@Column(name="coupon_name", length=30,unique=true)
	private String couponName;
	@Column(name="description")
	private String description;
	@Column(name="base_coupon_code")
	private String baseCouponCode;
	@Column(name="display_name")
	private String displayName;
	@Column(name="start_date")
	private LocalDate startDate;
	@Column(name="end_date")
	private LocalDate endDate;
	@Column(name = "image_name")
	private String imageName;
	@Column(name="status")
	private String status;
	@Column(name="tnc")
	private String tnc;
	@Column(name="total_usage")
	public Long totalUsage;
	@Column(name="no_of_unique_users")
	public Long noOfUniqueUsers;
	@Column(name="applicable_value")
	private String applicableForValue;
	@Column(name="tnc_end_date")
	private LocalDate tncEndDate;
	@Column(name="sequence")
	public Integer sequence;
	@Column(name="create_date")
	private LocalDateTime createDate;
	@Column(name="modified_date")
	private LocalDateTime modifiedDate;
	@Column(name="first_expiry_date")
	private LocalDate firstExpiryDate;

	@Column(name="extended_expiry_date")
	private LocalDate extendedExpiryDate;

	@Column(name="number_of_times_applicable_per_user")
	private Integer numberOfTimesApplicablePerUser;

	@Column(name = "channel")
	private String channel;
	@Column(name = "channel_fulfillment_type")
	private String channelFulfillmentType;
	@Column(name = "franchise")
	private String franchise;
	@Column(name = "stores")
	private String stores;
	@Column(name = "cities")
	private String cities;
	@Column(name = "clusters")
	private String clusters;
	@Column(name = "day_applicability")
	private String dayApplicability;
	@Column(name = "month_applicability")
	private String monthApplicability;
	@Column(name = "timeslot")
	private Timeslot timeslot;
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "coupon_id")
	private List<ItemsEntity> freebieItems;
	@Column(name="discount_percentage")
	private Double discountPercentage;

	@Column(name="flat_discount")
	private Double flatDiscount;

	@Column(name="discount_cap")
	private Double discountCap;

	@Column(name="minimum_order_value")
	private Double minimumOrderValue;

	@Column(name="product_inclusion")
	private String productInclusion;

	@Column(name="product_exclusion")
	private String productExclusion;

	@ManyToOne
	@JoinColumn(name = "segment_id")
	private SegmentDetailsEntity applicableForSegments;
	@Column(name = "user_attached_date")
	private LocalDate userAttachedDate;
	@Column(name="extended_number_of_days")
	private Integer extendedNumberOfDays;

	@Column(name="number_of_days")
	private Integer numberOfDays;
	@Column(name = "lot_name")
	private String lotName;
	@Column(name = "coupon_type")
	private String couponType;
	@Column(name = "user_specific_constraints")
	private Boolean userSpecificConstraints;
	@Column(name = "constraint_type")
	private String constraintType;
	@Column(name = "hidden_ui")
	private Boolean ishiddenUI;
	@Column(name = "category_inclusion")
	private String categoryInclusion;
	@Column(name = "category_exclusion")
	private String categoryExclusion;
	@Column(name = "prefix")
	private String prefix;
	@Column(name = "suffix")
	private String suffix;
	@Column(name = "usage_type")
	private String usageType;        //Newly added by Manish , Value can be system generated or coupon upload
	@Column(name = "batch_lot_name")
	private String batchLotName;
	@Column(name = "quantity")
	private Integer quantity;



	public List<String> getChannel() {
		return Arrays.asList(channel.split(","));
	}
	public void setChannel(List<String> channel) {
		this.channel = String.join(",", channel);
	}

	public List<String> getChannelFulfillmentType() {
		return Arrays.asList(channelFulfillmentType.split(","));
	}

	public void setChannelFulfillmentType(List<String> channelFulfillmentType) {
		this.channelFulfillmentType = String.join(",", channelFulfillmentType);
	}
	public List<String> getFranchise() {
		return Arrays.asList(channel.split(","));
	}
	public  void setFranchise(List<String> franchise) {
		this.franchise = String.join(",", franchise);
	}
	public List<String> getStores() {
		return Arrays.asList(stores.split(","));
	}
	public void setStores(List<String> stores) {
		this.stores = String.join(",", stores);
	}
	public List<String> getCities() {
		return Arrays.asList(cities.split(","));
	}
	public void setCities(List<String> cities) {
		this.cities = String.join(",", cities);
	}
	public List<String> getClusters() {
		return Arrays.asList(clusters.split(","));
	}
	public void setClusters(List<String> clusters) {
		this.clusters = String.join(",", clusters);
	}
	public List<String> getDayApplicability() {
		return Arrays.asList(dayApplicability.split(","));
	}
	public void setDayApplicability(List<String> dayApplicability) {
		this.dayApplicability = String.join(",", dayApplicability);
	}
	public List<Integer> getMonthApplicability() {
		if (monthApplicability == null || monthApplicability.isEmpty()) {
			return new ArrayList<>();
		}
		return Arrays.stream(monthApplicability.split(","))
				.map(Integer::parseInt)
				.collect(Collectors.toList());
	}
	public void setMonthApplicability(List<Integer> monthApplicability) {
		if (monthApplicability == null || monthApplicability.isEmpty()) {
			this.monthApplicability = "";
		} else {
			this.monthApplicability = monthApplicability.stream()
					.map(String::valueOf)
					.collect(Collectors.joining(","));
		}
	}
	public String getStatus() {
		return status;
	}
	public List<String> getCategoryInclusion() {
		return Arrays.asList(channel.split(","));
	}
	public void setCategoryInclusion(List<String> channel) {
		this.channel = String.join(",", channel);
	}
	public List<String> getCategoryExclusion() {
		return Arrays.asList(channel.split(","));
	}
	public void setCategoryExclusion(List<String> channel) {
		this.channel = String.join(",", channel);
	}
	public List<String> getProductInclusion() {
		return Arrays.asList(productInclusion.split(","));
	}
	public void setProductInclusion(List<String> productInclusion) {
		this.productInclusion = String.join(",", productInclusion);
	}
	public List<String> getProductExclusion() {
		return Arrays.asList(productExclusion.split(","));
	}
	public void setProductExclusion(List<String> productExclusion) {
		this.productExclusion = String.join(",", productExclusion);
	}

}
