package com.project.coupon.exceptions;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
public class MovInvalidException extends RuntimeException{

    private double mov;
    private List<String> productInclusion;
    private List<String> productExclusion;
    private List<String> categoryInclusion;
    private List<String> categoryExclusion;

    public MovInvalidException()
    {
        super("MOV not satisfied");
    }

    public MovInvalidException(String message,double mov,List<String> productInclusion,List<String> productExclusion,List<String> categoryInclusion,List<String> categoryExclusion)
    {
        super(message);
        this.mov=mov;
        this.productExclusion=productExclusion;
        this.productInclusion=productInclusion;
        this.categoryExclusion=categoryExclusion;
        this.categoryInclusion=categoryInclusion;
    }


}
