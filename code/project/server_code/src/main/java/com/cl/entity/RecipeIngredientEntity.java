package com.cl.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("recipe_ingredient")
public class RecipeIngredientEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long recipeId;

    private String ingredientCode;

    private String ingredientName;

    private Double weight;

    private String unit;

    private Double baseCalories;

    private Double calories;
}