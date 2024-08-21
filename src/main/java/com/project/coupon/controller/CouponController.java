package com.project.coupon.controller;
import com.project.coupon.constants.CouponConstant;
import com.project.coupon.exceptions.CouponException;
import com.project.coupon.request.*;
import com.project.coupon.response.*;
import com.project.coupon.service.ExcelFileSave;
import com.project.coupon.service.ExcelStoreFileSave;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.project.coupon.service.CouponService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/coupon")
@Log4j2
public class CouponController {
	@Autowired
	CouponService couponService;

	@Autowired
	ExcelFileSave excelFileSaveService;

	@Autowired
	ExcelStoreFileSave excelStoreFileSaveService;
	
	@PostMapping("/createcoupun")
	public String createCoupon(@RequestBody CouponRequest req)
	{
		return couponService.createCoupon(req);
	}
	
	
	@PostMapping("/createmongocoupon")
	public String createMongoCoupon(@RequestBody CouponRequest req) {
	       return couponService.createMongoCoupon(req);

	}
	
	@PostMapping("/createbasecoupon")
	public BaseCouponResponse createBaseCoupon(@RequestBody BaseCouponRequest req) throws Exception {
	       return couponService.createBaseCoupon(req);       
	}

	@PostMapping("/updatebasecoupon/{basecouponname}")
	public BaseCouponResponse createBaseCoupon(@PathVariable("basecouponname") String baseCouponName,@RequestBody BaseCouponRequest req) throws Exception {
	       return couponService.updateBaseCoupon(baseCouponName, req);       
	}
	
	@PostMapping("/createuniquecoupon")
	public UniqueCouponResponse createUniqueCoupon(@RequestBody UniqueCouponRequest req) throws Exception {
		req.setStatus(CouponConstant.CREATED);
		return couponService.createUniqueCoupon(req);
	}
	
	@PostMapping("/updateuniquecoupon")
	public UniqueCouponResponse updateUniqueCoupon(@RequestBody UniqueCouponRequest req) throws Exception {
	       return couponService.updateUniqueCoupon(req);       
	}
	@GetMapping("/getuniquecoupon/{uniquecouponname}")
	public UniqueCouponResponse getUniqueCoupon(@PathVariable("uniquecouponname") String uniquecouponname)
	{
	       return couponService.getUniqueCoupon(uniquecouponname);       
	}
	@GetMapping("/getbasecoupon/{basecouponname}")
	public BaseCouponResponse getBaseCoupon(@PathVariable("basecouponname") String baseCouponName)
	{
	       return couponService.getBaseCoupon(baseCouponName);       
	}
	
	@GetMapping("/getallbasecoupon")
	public AllBaseCouponResponse getAllBaseCoupon()
	{
	       return couponService.getAllBaseCoupon();       
	}
	
	@PostMapping("/upload/{type}")
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,@PathVariable("type") String type) {
		if (file.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select a file to upload");
		}

		try {
			if(type.equals("product"))
			{
				excelFileSaveService.save(file);
			}
			else {
				excelStoreFileSaveService.save(file);
			}

			return ResponseEntity.status(HttpStatus.OK).body("File uploaded and data saved successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while uploading the file: " + e.getMessage());
		}
	}
	@PutMapping("/update-date-range/base")
	public ResponseEntity<String> updateBaseCouponDateRange(
			@RequestParam List<String> baseCouponCodes,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		try {
			couponService.updateBaseCouponsDateRange(baseCouponCodes, startDate, endDate);
			return new ResponseEntity<>("Base coupons date range updated successfully.", HttpStatus.OK);
		} catch (CouponException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/update-date-range/batch")
	public ResponseEntity<String> updateBatchCouponDateRange(
			@RequestParam List<String> batchIds,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		try {
			couponService.updateBatchCouponsDateRange(batchIds, startDate, endDate);
			return new ResponseEntity<>("Batch coupons date range updated successfully.", HttpStatus.OK);
		} catch (CouponException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PutMapping("/update-date-range/unique")
	public ResponseEntity<String> updateUniqueCouponDateRange(
			@RequestParam List<String> uniqueCouponCodes,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		try {
			couponService.updateUniqueCouponsDateRange(uniqueCouponCodes, startDate, endDate);
			return new ResponseEntity<>("Unique coupons date range updated successfully.", HttpStatus.OK);
		} catch (CouponException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PutMapping("/delete/base")
	public ResponseEntity<String> deleteBaseCoupons(@RequestBody List<String> baseCouponCodes) {
		try {
			log.info("Deleting base coupons: " + baseCouponCodes);
			couponService.deleteBaseCoupons(baseCouponCodes);
			return new ResponseEntity<>("Base coupons  deleted successfully.", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("Error occurred while  deleting base coupons: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PutMapping("/delete/batch")
	public ResponseEntity<String> deleteBatchCoupons(@RequestBody List<String> batchIds) {
		try {
			couponService.deleteBatchCoupons(batchIds);
			return new ResponseEntity<>("Batch coupons  deleted successfully.", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("Error occurred while  deleting batch coupons: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PutMapping("/delete/unique")
	public ResponseEntity<String> deleteUniqueCoupons(@RequestBody List<String> uniqueCouponCodes) {
		try {
			couponService.deleteUniqueCoupons(uniqueCouponCodes);
			return new ResponseEntity<>("Unique coupons  deleted successfully.", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("Error occurred while  deleting unique coupons: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PostMapping("/createUserSpecificConstraints")
	public UserSpecificConstraintsResponse createUserSpecificConstraints(@RequestBody UserSpecificConstraintsRequest request) throws Exception {
		return couponService.createUserSpecificConstraints(request);
	}

	@PutMapping("/updateUserSpecificConstraints")
	public UserSpecificConstraintsResponse updateUserSpecificConstraints(@RequestBody UserSpecificConstraintsRequest request) throws Exception {
		return couponService.createUserSpecificConstraints(request);
	}

	@PostMapping("/createCouponApplicability")
	public CouponApplicabilityResponse createCouponApplicability(@RequestBody CouponApplicabilityRequest request) throws Exception {
		return  couponService.createCouponApplicability(request);
	}

	@PutMapping("/updateCouponApplicability")
	public CouponApplicabilityResponse updateCouponApplicability(@RequestBody CouponApplicabilityRequest request) throws Exception {
		return  couponService.createCouponApplicability(request);
	}

	@PostMapping("/createCouponConstruct")
	public CouponConstructResponse createCouponConstruct(@RequestBody CouponConstructRequest request) throws Exception {
		return couponService.createCouponConstruct(request);
	}

	@PutMapping("/createCouponConstruct")
	public CouponConstructResponse updateCouponConstruct(@RequestBody CouponConstructRequest request) throws Exception {
		return couponService.createCouponConstruct(request);
	}

	@PostMapping("/uploadsegment/applicablefor/{couponcode}")
	public ResponseEntity<BaseResponse> uploadSegment(@PathVariable String couponcode,MultipartFile file) throws Exception {

		return new ResponseEntity<>(couponService.uploadSegmentforApplicableFor(file,couponcode),HttpStatus.OK) ;

	}

	@PostMapping(value="/uploadsegment/batchcoupons/{couponcode}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<BaseResponse> uploadSegmentForBatchCoupons(@PathVariable String couponcode,@RequestParam("file") MultipartFile file ,@RequestParam("prefix") String prefix,@RequestParam("postfix") String postfix) throws Exception {

		return new ResponseEntity<>(couponService.uploadSegmentForBatchCoupons(file,couponcode,prefix,postfix),HttpStatus.OK) ;

	}

	@PostMapping("/type-selection")
	public ResponseEntity<BaseResponse> selectCouponType(@RequestBody CouponTypeSelectionRequest request) throws Exception {
		BaseResponse response = couponService.handleCouponTypeSelection(request.getCouponType(), request.getCouponName());
		HttpStatus status = response.getStatus().equals(CouponConstant.SUCCESS) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
		return new ResponseEntity<>(response, status);
	}

	@PutMapping("/type-selection")
	public ResponseEntity<BaseResponse> updateSelectCouponType(@RequestBody CouponTypeSelectionRequest request) throws Exception {
		BaseResponse response = couponService.handleCouponTypeSelection(request.getCouponType(), request.getCouponName());
		HttpStatus status = response.getStatus().equals(CouponConstant.SUCCESS) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
		return new ResponseEntity<>(response, status);
	}


	@PostMapping("/usage/selection")
	public ResponseEntity<BaseResponse> couponUsageSelection(
			@RequestParam("couponName") String couponName,
			@RequestParam("usageType") String usageType,
			@RequestParam(required = false) String prefix,
			@RequestParam(required = false) String suffix,
			@RequestParam(required = false) Integer quantity,
			@RequestParam(required = false) String batchLotName,
			@RequestParam(required = false) MultipartFile file
	) throws Exception {
		CouponUsageSelectionRequest request = new CouponUsageSelectionRequest();
		request.setCouponName(couponName);
		request.setUsageType(usageType);

		// Handle system-generated option
		if (CouponConstant.COUPONUSAGETYPE_SYSTEMGENERATED.equalsIgnoreCase(usageType)) {
			if (file != null) {
				return new ResponseEntity<>(BaseResponse.builder()
						.status(CouponConstant.FAILURE)
						.message("File upload is not allowed for system-generated usage type.")
						.build(), HttpStatus.BAD_REQUEST);
			}
			request.setPrefix(prefix);
			request.setSuffix(suffix);
			request.setQuantity(quantity != null ? quantity : 0);
			request.setBatchLotName(batchLotName);

			return new ResponseEntity<>(couponService.createSystemGeneratedCoupons(request), HttpStatus.OK);

			// Handle coupon-upload option
		} else if (CouponConstant.COUPONUSAGETYPE_COUPONUPLOAD.equalsIgnoreCase(usageType)) {
			if (file == null) {
				return new ResponseEntity<>(BaseResponse.builder()
						.status(CouponConstant.FAILURE)
						.message("File must be uploaded for coupon-upload usage type.")
						.build(), HttpStatus.BAD_REQUEST);
			}
			request.setFile(file);
			return new ResponseEntity<>(couponService.uploadCouponFile(request), HttpStatus.OK);
		}

		// Invalid usage type
		return new ResponseEntity<>(BaseResponse.builder()
				.status(CouponConstant.FAILURE)
				.message("Invalid usage type selected.")
				.build(), HttpStatus.BAD_REQUEST);
	}

	@PutMapping("/usage/selection")
	public ResponseEntity<BaseResponse> updateCouponUsageSelection(
			@RequestParam("couponName") String couponName,
			@RequestParam("usageType") String usageType,
			@RequestParam(required = false) String prefix,
			@RequestParam(required = false) String suffix,
			@RequestParam(required = false) Integer quantity,
			@RequestParam(required = false) String batchLotName,
			@RequestParam(required = false) MultipartFile file
	) throws Exception {
		CouponUsageSelectionRequest request = new CouponUsageSelectionRequest();
		request.setCouponName(couponName);
		request.setUsageType(usageType);

		// Handle system-generated option
		if (CouponConstant.COUPONUSAGETYPE_SYSTEMGENERATED.equalsIgnoreCase(usageType)) {
			if (file != null) {
				return new ResponseEntity<>(BaseResponse.builder()
						.status(CouponConstant.FAILURE)
						.message("File upload is not allowed for system-generated usage type.")
						.build(), HttpStatus.BAD_REQUEST);
			}
			request.setPrefix(prefix);
			request.setSuffix(suffix);
			request.setQuantity(quantity != null ? quantity : 0);
			request.setBatchLotName(batchLotName);

			return new ResponseEntity<>(couponService.createSystemGeneratedCoupons(request), HttpStatus.OK);

			// Handle coupon-upload option
		} else if (CouponConstant.COUPONUSAGETYPE_COUPONUPLOAD.equalsIgnoreCase(usageType)) {
			if (file == null) {
				return new ResponseEntity<>(BaseResponse.builder()
						.status(CouponConstant.FAILURE)
						.message("File must be uploaded for coupon-upload usage type.")
						.build(), HttpStatus.BAD_REQUEST);
			}
			request.setFile(file);
			return new ResponseEntity<>(couponService.uploadCouponFile(request), HttpStatus.OK);
		}

		// Invalid usage type
		return new ResponseEntity<>(BaseResponse.builder()
				.status(CouponConstant.FAILURE)
				.message("Invalid usage type selected.")
				.build(), HttpStatus.BAD_REQUEST);
	}

	@PostMapping("/upload/image/{code}")
	ResponseEntity<BaseResponse> uploadCouponImage(@RequestParam MultipartFile file,@PathVariable String code) throws Exception {

		return new ResponseEntity<>(couponService.uploadCouponImage(file,code),HttpStatus.OK) ;

	}

	@PostMapping("/createmilestonecoupons")
	ResponseEntity<BaseResponse> createMilestoneCoupons(@RequestBody MilestoneRequest request){
		return new ResponseEntity<>(couponService.createMilestoneCoupon(request),HttpStatus.OK);
	}

	@PostMapping("uploadsegmentformilestone/{journeyName}")
	ResponseEntity<BaseResponse> uploadSegmentForMilestoneCoupon(MultipartFile file,@PathVariable String journeyName) throws Exception {

		return new ResponseEntity<>(couponService.uploadSegmentforMilestoneCoupons(file,journeyName),HttpStatus.OK);

	}




	@PostMapping("/upload_items/{uniquecouponname}")
	public ResponseEntity<String> uploadItems(@PathVariable String uniquecouponname,
											  @RequestParam("type") String type,
											  @RequestParam("file") MultipartFile file) {
		String fileType = file.getContentType();
		String fileName = file.getOriginalFilename();
		if (fileName == null || (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx"))) {
			throw new IllegalArgumentException("Invalid file type. Only .xls and .xlsx files are accepted.");
		}

		try {
			// Process the uploaded file based on the type
			switch (type.toUpperCase()) {
				case CouponConstant.TEMPLATE_FREEBIE:
					couponService.processFreebieFile(file, uniquecouponname);
					break;
				case CouponConstant.TEMPLATE_PRODUCT_INCLUSION:
					couponService.processProductInclusionFile(file, uniquecouponname);
					break;
				case CouponConstant.TEMPLATE_PRODUCT_EXCLUSION:
					couponService.processProductExclusionFile(file, uniquecouponname);
					break;
				case CouponConstant.TEMPLATE_STORE:
					couponService.processStoreFile(file, uniquecouponname);
					break;
				default:
					throw new IllegalArgumentException("Invalid type specified");
			}
			return ResponseEntity.ok("File uploaded and processed successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the file: " + e.getMessage());
		}
	}
	@GetMapping("/download_general/{type}")
	public ResponseEntity<Resource> downloadTemplate(@PathVariable String type) throws IOException {
		String fileName;
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Template");
		Row headerRow = sheet.createRow(0);

		// Create headers based on the type
		switch (type.toUpperCase()) {
			case CouponConstant.TEMPLATE_FREEBIE:
				fileName = "freebie_template.xlsx";
				headerRow.createCell(0).setCellValue("item_code");
				headerRow.createCell(1).setCellValue("price");
				headerRow.createCell(2).setCellValue("quantity");
				headerRow.createCell(3).setCellValue("item_description");
				headerRow.createCell(4).setCellValue("size");
				break;
			case CouponConstant.TEMPLATE_PRODUCT_INCLUSION:
				fileName = "product_inclusion_template.xlsx";
				headerRow.createCell(0).setCellValue("product_code");
				headerRow.createCell(1).setCellValue("product_name");
				headerRow.createCell(2).setCellValue("category");
				break;
			case CouponConstant.TEMPLATE_PRODUCT_EXCLUSION:
				fileName = "product_exclusion_template.xlsx";
				headerRow.createCell(0).setCellValue("product_code");
				headerRow.createCell(1).setCellValue("product_name");
				headerRow.createCell(2).setCellValue("category");
				break;
			case CouponConstant.TEMPLATE_STORE:
				fileName = "store_template.xlsx";
				headerRow.createCell(0).setCellValue("store_code");
				headerRow.createCell(1).setCellValue("store_name");
				break;
			default:
				throw new IllegalArgumentException("Invalid template type");
		}

		// Write the workbook to an output stream
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		workbook.write(outputStream);
		workbook.close();

		// Convert the output stream to a byte array resource
		ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

		// Return the response with the file attached
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(resource);
	}





}