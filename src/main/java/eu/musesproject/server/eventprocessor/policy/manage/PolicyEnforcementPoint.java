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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.balana.*;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.xacml3.Result;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.AttributeFinderModule;
import org.wso2.balana.finder.ResourceFinder;
import org.wso2.balana.finder.ResourceFinderModule;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;
import org.wso2.balana.xacml3.Attributes;

import javax.xml.parsers.DocumentBuilderFactory;

import java.io.ByteArrayInputStream;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PolicyEnforcementPoint {
	
	private static Balana balana;
	
	public static String authorizedResources(String user, String resourceType){

		String authorizedResult = null;
        Console console;
        String userName = null;
        String type = null;

        initBalana();

        System.out.println("\nYou can check the authorization for children or descendants resource " +
                                                                        "under root resources \n");

        System.out.println("root-");
        System.out.println("    ------ private ---- leadership");
        System.out.println("    -              ---- support");
        System.out.println("    -              ---- team");
        System.out.println("    -              ---- business");
        System.out.println("    -");
        System.out.println("    ------ public  ---- developments");
        System.out.println("    -              ---- news");
        System.out.println();

        if ((console = System.console()) != null){
            userName = console.readLine("\nCheck authorized resources for user : ");
        }else{
        	userName = user;
        }

        if ((console = System.console()) != null){
            type = console.readLine("\nDescendants or Children resources [D|C] : ");
        }else{
        	type = resourceType;
        }

        if(userName != null && userName.trim().length() > 0){

            if(type != null && type.toLowerCase().equals("d")){
                type = "Descendants";
            } else {
                type = "Children";
            }

            String request = createXACMLRequest(userName, type);
            PDP pdp = PolicyDecisionPoint.getPDPNewInstance();

            System.out.println("\n======================== XACML Request ====================");
            System.out.println(request);
            System.out.println("===========================================================");

            String response = pdp.evaluate(request);

            System.out.println("\n======================== XACML Response ===================");
            System.out.println(response);
            System.out.println("===========================================================");

            Set<String> permitResources = new HashSet<String>();
            Set<String> denyResources = new HashSet<String>();

            try {
                ResponseCtx responseCtx = ResponseCtx.getInstance(getXacmlResponse(response));
                Set<AbstractResult> results  = responseCtx.getResults();
                for(AbstractResult result : results){
                    Set<Attributes> attributesSet = ((Result)result).getAttributes();
                    for(Attributes attributes : attributesSet){
                        for(Attribute attribute : attributes.getAttributes()){
                            if(AbstractResult.DECISION_PERMIT == result.getDecision()){
                                permitResources.add(attribute.getValue().encode());
                            } else {
                                denyResources.add(attribute.getValue().encode());
                            }
                        }
                    }
                }
            } catch (ParsingException e) {
                e.printStackTrace(); 
            }

            if(permitResources.size() > 0){
                authorizedResult = "\n" + userName + " is authorized for following resources...\n";
                for(String result : permitResources){
                    authorizedResult = result + "\t";
                }
                authorizedResult = "\n";
            } else {
            	authorizedResult = "\n" + userName + " is NOT authorized access any resource..!!!\n";
            }

        } else {
            System.err.println("\nUser name can not be empty\n");                
        }
        
        return authorizedResult;
    }

    private static void initBalana(){

        try{
            // using file based policy repository. so set the policy location as system property
            String policyLocation = (new File(".")).getCanonicalPath() + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "policies";
            System.out.println(policyLocation);
            System.setProperty(FileBasedPolicyFinderModule.POLICY_DIR_PROPERTY, policyLocation);
        } catch (IOException e) {
            System.err.println("Can not locate policy repository");
        }
        // create default instance of Balana
        balana = Balana.getInstance();
    }

    /**
     * Returns a new PDP instance with new XACML policies
     *
     * @return a  PDP instance
     */
    /*public static PDP getPDPNewInstance(){

        PDPConfig pdpConfig = balana.getPdpConfig();

        // registering new attribute finder. so default PDPConfig is needed to change
        AttributeFinder attributeFinder = pdpConfig.getAttributeFinder();
        List<AttributeFinderModule> finderModules = attributeFinder.getModules();
        finderModules.add(new MusesAttributeFinderModule());
        attributeFinder.setModules(finderModules);

        // registering new resource finder. so default PDPConfig is needed to change
        ResourceFinder resourceFinder = pdpConfig.getResourceFinder();
        List<ResourceFinderModule> resourceModules = resourceFinder.getModules();
        resourceModules.add(new HierarchicalResourceFinder());
        resourceFinder.setModules(resourceModules);


        return new PDP(new PDPConfig(attributeFinder, pdpConfig.getPolicyFinder(), resourceFinder, true));
    }*/


    /**
     * Creates DOM representation of the XACML request
     *
     * @param response  XACML request as a String object
     * @return XACML request as a DOM element
     */
    public static Element getXacmlResponse(String response) {

        ByteArrayInputStream inputStream;
        DocumentBuilderFactory dbf;
        Document doc;

        inputStream = new ByteArrayInputStream(response.getBytes());
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        try {
            doc = dbf.newDocumentBuilder().parse(inputStream);
        } catch (Exception e) {
            System.err.println("DOM of request element can not be created from String");
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
               System.err.println("Error in closing input stream of XACML response");
            }
        }
        return doc.getDocumentElement();
    }

    public static String createXACMLRequest(String userName, String type){

        return "<Request xmlns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" CombinedDecision=\"false\" ReturnPolicyIdList=\"false\">\n" +
                "<Attributes Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:action\">\n" +
                "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" IncludeInResult=\"false\">\n" +
                "<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">access</AttributeValue>\n" +
                "</Attribute>\n" +
                "</Attributes>\n" +
                "<Attributes Category=\"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\">\n" +
                "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:subject-id\" IncludeInResult=\"false\">\n" +
                "<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">"+ userName +"</AttributeValue>\n" +
                "</Attribute>\n" +
                "</Attributes>\n" +
                "<Attributes Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\">\n" +
                "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:resource:resource-id\" IncludeInResult=\"true\">\n" +
                "<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">root</AttributeValue>\n" +
                "</Attribute>\n" +
                "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:2.0:resource:scope\" IncludeInResult=\"false\">\n" +
                "<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">" + type + "</AttributeValue>\n" +
                "</Attribute>\n" +
                "</Attributes>\n" +
                "</Request>";

    }
    
    public static Balana getBalana(){
    	if (balana==null){
    		initBalana();
    	}
    	return balana;
    }

}
