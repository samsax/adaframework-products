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

package es.androcode.adafw.extras;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.desandroid.framework.ada.DataBinder;
import com.desandroid.framework.ada.Entity;
import com.desandroid.framework.ada.exceptions.AdaFrameworkException;
import com.desandroid.framework.ada.validators.ValidationResult;

import es.androcode.adafw.products.db.ApplicationDataContext;
import es.androcode.adafw.products.model.Category;

public class EditCategoryActivity extends Activity {

    public static final String ID_CATEGORY = "idCategory";
    /**
     * Activity DataContext.
     */
    private ApplicationDataContext appDataContext;
    private Category category;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_category);
        
        long idCategory = getIntent().getLongExtra(ID_CATEGORY, 0);
        category = queryCategory(idCategory);
        if (category == null) {
            finish();
        } else {
        	try {
				category.bind(this);
			} catch (AdaFrameworkException e) {
				Log.e("Androcode", "Error: " + e.getMessage());
			}
            setResult(RESULT_CANCELED);
        }
    }
    
    public void save(View view) {
    	try {
			category.bind(this, DataBinder.BINDING_UI_TO_ENTITY);
			if (category.Validate(this)) {
				ApplicationDataContext dataContext = getApplicationDataContext();
				dataContext.categoryDao.save(category);
				setResult(RESULT_OK);
				finish();
			} else {
				List<ValidationResult> lst = category.getValidationResult();
				StringBuilder sb = new StringBuilder(getString(R.string.errors));
				for (ValidationResult vr : lst) {
					sb.append("\n");
					sb.append(vr.getMessage());
				}
				Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
			}
		} catch (AdaFrameworkException e) {
			Log.e("Androcode", "Error guardando el producto: " + e.getMessage());
		}
    }

    public void cancel(View view) {
        finish();
    }
    
    private ApplicationDataContext getApplicationDataContext() {
        if (appDataContext == null) {
            try {
                appDataContext = new ApplicationDataContext(this);
            } catch (Exception e) {
                Log.e("Androcode", "Error inicializando ApplicationDataContext: " + e.getMessage());
            }
        }
        return appDataContext;
    }
    
    private Category queryCategory(long idCategory) {
        Category category = null;
        ApplicationDataContext dataContext = getApplicationDataContext();
        if (dataContext != null) {            
            if (idCategory == 0) {
                // Es una categoría nueva, creamos el objeto
                category = new Category("", "");
                category.setStatus(Entity.STATUS_NEW);
            } else {
                // Recuperamos la categoría
                try {
                    category = dataContext.categoryDao.getElementByID(idCategory);
                    category.setStatus(Entity.STATUS_UPDATED);
                } catch (Exception e) {
                    Log.e("Androcode", "Error recuperando categoria: " + e.getMessage());
                }
                ((TextView) findViewById(R.id.edit_title)).setText(R.string.edit_category);
            }
        }
        return category;
    }

}
