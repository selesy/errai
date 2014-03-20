package org.jboss.errai.security.client.shared;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jboss.errai.security.shared.api.annotation.RestrictAccess;

@Remote
public interface  ServiceInterface {
  @RestrictAccess(roles = "admin")
  void annotatedServiceMethod();
}