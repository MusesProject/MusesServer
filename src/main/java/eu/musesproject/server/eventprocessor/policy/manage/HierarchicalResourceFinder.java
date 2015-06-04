package eu.musesproject.server.eventprocessor.policy.manage;

/*
 * #%L
 * MUSES Server
 * %%
 * Copyright (C) 2013 - 2014 S2 Grupo
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.wso2.balana.Balana;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.finder.ResourceFinderModule;
import org.wso2.balana.finder.ResourceFinderResult;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

/**
 * Sample resource finder for finding hierarchical resources under the root node
 */
public class HierarchicalResourceFinder extends ResourceFinderModule {

    private final static String DATA_TYPE = "http://www.w3.org/2001/XMLSchema#string" ;

    @Override
    public boolean isChildSupported() {
        return true;
    }

    @Override
    public boolean isDescendantSupported() {
        return true;
    }

    @Override
    public ResourceFinderResult findChildResources(AttributeValue parentResourceId, EvaluationCtx context) {

        ResourceFinderResult result = new ResourceFinderResult();

        if(!DATA_TYPE.equals(parentResourceId.getType().toString())){
            return result;
        }

        if("root".equals(parentResourceId.encode())){
            Set<AttributeValue> set = new HashSet<AttributeValue>();
            try{
                set.add(Balana.getInstance().getAttributeFactory().createValue(new URI(DATA_TYPE), "private"));
                set.add(Balana.getInstance().getAttributeFactory().createValue(new URI(DATA_TYPE), "public"));
            } catch (Exception e) {
                // just ignore
            }
            result = new ResourceFinderResult(set);
        }

        return result;
    }


    @Override
    public ResourceFinderResult findDescendantResources(AttributeValue parentResourceId, EvaluationCtx context) {

        ResourceFinderResult result = new ResourceFinderResult();

        if(!DATA_TYPE.equals(parentResourceId.getType().toString())){
            return result;
        }

        if("root".equals(parentResourceId.encode())){
            Set<AttributeValue> set = new HashSet<AttributeValue>();
            try{
                set.add(Balana.getInstance().getAttributeFactory().createValue(new URI(DATA_TYPE), "private"));
                set.add(Balana.getInstance().getAttributeFactory().createValue(new URI(DATA_TYPE), "public"));
                set.add(Balana.getInstance().getAttributeFactory().createValue(new URI(DATA_TYPE), "public/developments"));
                set.add(Balana.getInstance().getAttributeFactory().createValue(new URI(DATA_TYPE), "public/news"));
                set.add(Balana.getInstance().getAttributeFactory().createValue(new URI(DATA_TYPE), "private/leadership"));
                set.add(Balana.getInstance().getAttributeFactory().createValue(new URI(DATA_TYPE), "private/business"));
                set.add(Balana.getInstance().getAttributeFactory().createValue(new URI(DATA_TYPE), "private/support"));
                set.add(Balana.getInstance().getAttributeFactory().createValue(new URI(DATA_TYPE), "private/team"));
            } catch (Exception e) {
                // just ignore
            }
            result = new ResourceFinderResult(set);
        }
        return result;
    }
}
