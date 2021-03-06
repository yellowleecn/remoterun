/*
 * Copyright 2015 Formicary Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.formicary.remoterun.common;

import net.formicary.remoterun.common.proto.RemoteRun;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.DownstreamMessageEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Netty logging handler that logs only sent and received messages.
 *
 * @author Chris Pearson
 */
public class NettyLoggingHandler extends LoggingHandler {
  private static final Logger log = LoggerFactory.getLogger(NettyLoggingHandler.class);

  @Override
  public void log(ChannelEvent e) {
    if(e instanceof MessageEvent) {
      Object message = ((MessageEvent)e).getMessage();
      if(message instanceof RemoteRun.AgentToMaster) {
        log.debug("{} {}: {}", e.getChannel().toString(), e instanceof DownstreamMessageEvent ? "WRITE" : "RECEIVED", toString((RemoteRun.AgentToMaster)message));
      } else if(message instanceof RemoteRun.MasterToAgent) {
        log.debug("{} {}: {}", e.getChannel().toString(), e instanceof DownstreamMessageEvent ? "WRITE" : "RECEIVED", toString((RemoteRun.MasterToAgent)message));
      } else {
        log.debug("{}", e);
      }
    }
  }

  private String toString(RemoteRun.AgentToMaster message) {
    // todo: is it possible to suppress fragment from protobuf default toString?
    StringBuilder sb = new StringBuilder();
    sb.append("messageType=").append(message.getMessageType());
    if(message.hasRequestId()) {
      sb.append(" requestId=").append(message.getRequestId());
    }
    if(message.hasFragment()) {
      sb.append(" fragment=[").append(message.getFragment().size()).append(" bytes]");
    }
    if(message.hasExitCode()) {
      sb.append(" exitCode=").append(message.getExitCode());
    }
    if(message.hasExitReason()) {
      sb.append(" exitReason=").append(message.getExitReason());
    }
    if(message.hasAgentInfo()) {
      RemoteRun.AgentToMaster.AgentInfo a = message.getAgentInfo();
      sb.append(" agentInfo=[")
        .append("hostname=").append(a.getHostname())
        .append(", ip=").append(ipToString(a.getIpAddress().toByteArray()))
        .append(", ").append(a.getEnvironmentCount()).append(" environment variables]");
    }
    return sb.toString();
  }

  private String ipToString(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < bytes.length; i++) {
      byte next = bytes[i];
      if(i > 0) {
        sb.append('.');
      }
      sb.append(next & 0xff);
    }
    return sb.toString();
  }

  private String toString(RemoteRun.MasterToAgent message) {
    // todo: is it possible to suppress fragment from protobuf default toString?
    StringBuilder sb = new StringBuilder();
    sb.append("messageType=").append(message.getMessageType());
    if(message.hasRequestId()) {
      sb.append(" requestId=").append(message.getRequestId());
    }
    if(message.hasRunCommand()) {
      sb.append(" runCommand=").append(message.getRunCommand());
    }
    if(message.hasFragment()) {
      sb.append(" fragment=[").append(message.getFragment().size()).append(" bytes]");
    }
    if(message.hasDataSuccess()) {
      sb.append(" dataSuccess=").append(message.getDataSuccess());
    }
    if(message.hasPath()) {
      sb.append(" path=").append(message.getPath());
    }
    return sb.toString();
  }
}
