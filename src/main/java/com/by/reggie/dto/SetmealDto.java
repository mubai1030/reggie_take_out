package com.by.reggie.dto;

import com.by.reggie.entity.Setmeal;
import com.by.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
