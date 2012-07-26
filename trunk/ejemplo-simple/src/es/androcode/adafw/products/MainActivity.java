/*
 * A project sample for AdaFramework (http://www.adaframework.com/)
 * 
 * Copyright (C) 2012 Androcode
 * <http://www.androcode.es>
 * 
 * This project is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.androcode.adafw.products;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    public void viewCategories(View view) {
        startActivity(new Intent(this, CategoriesActivity.class));
    }
    
    public void newCategory(View view) {
        startActivity(new Intent(this, EditCategoryActivity.class));
    }
    
    public void viewProducts(View view) {
        startActivity(new Intent(this, ProductsActivity.class));
    }
    
    public void newProduct(View view) {
        startActivity(new Intent(this, EditProductActivity.class));
    }
    
}