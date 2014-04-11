package eu.musesproject.server.contextdatareceiver;

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

import javax.servlet.ServletContext;

import org.picketbox.http.config.ConfigurationBuilderProvider;
import org.picketbox.http.config.HTTPConfigurationBuilder;
import org.picketbox.http.resource.ProtectedResourceConstraint;


public class MusesAuthenticationProvider implements ConfigurationBuilderProvider {

    /*
     * (non-Javadoc)
     * 
     * @see org.picketbox.http.config.ConfigurationBuilderProvider#getBuilder(javax.servlet.ServletContext)
     */
    @Override
    public HTTPConfigurationBuilder getBuilder(ServletContext servletcontext) {
        HTTPConfigurationBuilder configurationBuilder = new HTTPConfigurationBuilder();
        
        // protected resources configuration
        configurationBuilder.protectedResource()
                // unprotected resource. Usually this will be your application's static resources like CSS, JS, etc.
                .resource("/resources/*", ProtectedResourceConstraint.NOT_PROTECTED)
                // the login page is marked as not protected.
                .resource("/login.jsp", ProtectedResourceConstraint.NOT_PROTECTED)
                // the user register resources is marked as not protected.
                .resource("/services/register", ProtectedResourceConstraint.NOT_PROTECTED)
                .resource("/services/checkUsername", ProtectedResourceConstraint.NOT_PROTECTED)
                // the register page is marked as not protected.
                .resource("/signup", ProtectedResourceConstraint.NOT_PROTECTED)
                .resource("/signup.jsp", ProtectedResourceConstraint.NOT_PROTECTED)
                // the error page is marked as not protected.
                .resource("/error.jsp", ProtectedResourceConstraint.NOT_PROTECTED)
                // protected all resources. They will be available only for users with a role named 'guest'.
                .resource("/*", "guest");

        return configurationBuilder;
    }

}
