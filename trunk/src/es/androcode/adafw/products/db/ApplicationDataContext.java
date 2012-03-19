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

package es.androcode.adafw.products.db;

import android.content.Context;

import com.desandroid.framework.ada.ObjectContext;
import com.desandroid.framework.ada.ObjectSet;

import es.androcode.adafw.products.model.Category;
import es.androcode.adafw.products.model.Product;

/**
 * Class for entities ObjectSet
 * 
 * @author fedeproex
 * 
 */
public class ApplicationDataContext extends ObjectContext {

    public ObjectSet<Category> categoryDao;
    public ObjectSet<Product> productDao;

    /**
     * Constructor
     * 
     * @param pContext
     *            context
     * @throws Exception
     */
    public ApplicationDataContext(Context pContext) throws Exception {
        super(pContext);
        this.categoryDao = new ObjectSet<Category>(Category.class, this);
        this.productDao = new ObjectSet<Product>(Product.class, this);
    }

}
