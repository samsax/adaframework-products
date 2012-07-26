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

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.desandroid.framework.ada.DataBinder;
import com.desandroid.framework.ada.Entity;
import com.desandroid.framework.ada.ObjectSet;
import com.desandroid.framework.ada.exceptions.AdaFrameworkException;
import com.desandroid.framework.ada.validators.ValidationResult;

import es.androcode.adafw.products.db.ApplicationDataContext;
import es.androcode.adafw.products.model.Category;
import es.androcode.adafw.products.model.Product;

public class EditProductActivity extends Activity {

    public static final String ID_PRODUCT = "idProduct";
    /**
     * Activity DataContext.
     */
    private ApplicationDataContext appDataContext;
    private Button categoryName;
    private Product product;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_product);
        
        // Miramos si nos han enviado un identificador por extras
        long idProduct = getIntent().getLongExtra(ID_PRODUCT, 0);
        
        ApplicationDataContext dataContext = getApplicationDataContext();
        try {
        	// Recuperamos las categorías
            dataContext.categoryDao.fill("name");
            
            if (idProduct == 0) {
                // Es una producto nuevo, creamos el objeto
                product = new Product("", null, 0, 0, 0);
                product.setStatus(Entity.STATUS_NEW);                
            } else {
                // Es una edición, recuperamos el producto y cambiamos el título                
                product = dataContext.productDao.getElementByID(idProduct);
                product.setStatus(Entity.STATUS_UPDATED);
                ((TextView) findViewById(R.id.edit_title)).setText(R.string.edit_product);
                // Lo enlazamos a la vista
                product.bind(this);
            }
        } catch (Exception e) {
            Log.e("Androcode", "Error cargando categorias: " + e.getMessage());
        }
        setResult(RESULT_CANCELED);
    }
    
    public void save(View view) {
    	try {
			product.bind(this, DataBinder.BINDING_UI_TO_ENTITY);
			if (product.Validate(this)) {
				ApplicationDataContext dataContext = getApplicationDataContext();
				dataContext.productDao.save(product);
				setResult(RESULT_OK);
				finish();
			} else {
				List<ValidationResult> lst = product.getValidationResult();
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
    
    public void changeProductCategory(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        final ObjectSet<Category> objectSet = getApplicationDataContext().categoryDao;
        final String[] items = new String[objectSet.size() + 1];
        items[0] = getString(R.string.without_category);
        int pos = 1;
        for (Iterator<Category> it = objectSet.iterator(); it.hasNext();) {
            items[pos++] = it.next().getName();            
        }
        dialog.setItems(items, new DialogInterface.OnClickListener() {            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                categoryName.setText(items[which]);
                if (which == 0) {
                    product.setCategory(null);
                } else {
                    product.setCategory(objectSet.get(which - 1));                    
                }
            }
        });
        dialog.create().show();
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

}
