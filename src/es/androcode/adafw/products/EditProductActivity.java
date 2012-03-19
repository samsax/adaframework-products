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

import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.desandroid.framework.ada.Entity;
import com.desandroid.framework.ada.ObjectSet;

import es.androcode.adafw.products.db.ApplicationDataContext;
import es.androcode.adafw.products.model.Category;
import es.androcode.adafw.products.model.Product;

public class EditProductActivity extends Activity {

    public static final String ID_PRODUCT = "idProduct";
    /**
     * Activity DataContext.
     */
    private ApplicationDataContext appDataContext;
    private EditText name;
    private Button categoryName;
    private EditText quantity;
    private EditText price;
    private EditText stock;
    private Product product;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_product);
        name = (EditText) findViewById(R.id.name);
        categoryName = (Button) findViewById(R.id.category);
        quantity = (EditText) findViewById(R.id.quantity);
        price = (EditText) findViewById(R.id.price);
        stock = (EditText) findViewById(R.id.stock);
        
        long idProdcut = getIntent().getLongExtra(ID_PRODUCT, 0);
        product = queryProductAndCategories(idProdcut);
        if (product == null) {
            finish();
        } else {            
            name.setText(product.getName());
            quantity.setText(Integer.toString(product.getQuantityPerUnit()));
            price.setText(Double.toString(product.getUnitPrice()));
            stock.setText(Integer.toString(product.getUnitsInStock()));
            if (product.getCategory() != null) {
                categoryName.setText(product.getCategory().getName());                
            }
            setResult(RESULT_CANCELED);
        }
    }
    
    public void save(View view) {
        String newName = name.getText().toString();
        if (newName.trim().length() == 0) {
            Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
        //} else if (product.getCategory() == null) {
          //  Toast.makeText(this, "Debe seleccionar alguna categoría", Toast.LENGTH_SHORT).show();            
        } else {
            // Guardamos los nuevos datos
            product.setName(newName);
            product.setQuantityPerUnit(Integer.parseInt(quantity.getText().toString()));
            product.setUnitPrice(Double.parseDouble(price.getText().toString()));
            product.setUnitsInStock(Integer.parseInt(stock.getText().toString()));
            // Salvamos
            saveProduct();
            // Marcamos como que hemos realizado cambios
            setResult(RESULT_OK);
            finish();
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
    
    private void saveProduct() {
        ApplicationDataContext dataContext = getApplicationDataContext();
        if (dataContext != null) {
            try {                
                dataContext.productDao.save(product);
            } catch (Exception e) {
                Log.e("Androcode", "Error guardando categoria: " + e.getMessage());
            }
        }
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
    
    private Product queryProductAndCategories(long idProduct) {
        Product product = null;
        ApplicationDataContext dataContext = getApplicationDataContext();
        if (dataContext != null) {
            try {
                dataContext.categoryDao.fill("name");
            } catch (Exception e) {
                Log.e("Androcode", "Error cargando categorias: " + e.getMessage());
            }
            if (idProduct == 0) {
                // Es una producto nueva, creamos el objeto
                product = new Product("", null, 0, 0, 0);
                product.setStatus(Entity.STATUS_NEW);                
            } else {
                // Es una edición, recuperamos el producto y cambiamos el título                
                try {
                    product = dataContext.productDao.getElementByID(idProduct);
                    product.setStatus(Entity.STATUS_UPDATED);
                } catch (Exception e) {
                    Log.e("Androcode", "Error recuperando el producto: " + e.getMessage());
                }
                ((TextView) findViewById(R.id.edit_title)).setText(R.string.edit_product);
            }
        }
        return product;
    }

}
