/*
 *
 *  Copyright 2018 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.genie.common.internal.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * The builder and methods available for a request generated by a Genie agent typically from the command arguments.
 *
 * @author tgianos
 * @since 4.0.0
 */
@JsonDeserialize(builder = AgentJobRequest.Builder.class)
public interface AgentJobRequest extends CommonRequest {

    /**
     * Get the command arguments a user has requested be appended to a command executable for their job.
     *
     * @return The command arguments as an immutable list. Any attempt to modify will throw exception
     */
    List<String> getCommandArgs();

    /**
     * Get the metadata a user has supplied for the job including things like name, tags, etc.
     *
     * @return The metadata
     */
    JobMetadata getMetadata();

    /**
     * The resource criteria that was supplied for the job.
     *
     * @return The criteria used to select the cluster, command and optionally applications for the job
     */
    ExecutionResourceCriteria getCriteria();

    /**
     * Get the requested agent configuration.
     *
     * @return The requested agent configuration parameters
     */
    AgentConfigRequest getRequestedAgentConfig();

    /**
     * Builder for a V4 Job Request.
     *
     * @author tgianos
     * @since 4.0.0
     */
    @Getter(AccessLevel.PACKAGE)
    class Builder extends CommonRequestImpl.Builder<AgentJobRequest.Builder> {

        private final JobMetadata bMetadata;
        private final ExecutionResourceCriteria bCriteria;
        private final AgentConfigRequest bRequestedAgentConfig;
        private final List<String> bCommandArgs = Lists.newArrayList();

        /**
         * Constructor with required parameters.
         *
         * @param metadata             All user supplied metadata
         * @param criteria             All user supplied execution criteria
         * @param requestedAgentConfig The requested configuration of the Genie agent
         */
        @JsonCreator
        public Builder(
            @JsonProperty(value = "metadata", required = true) final JobMetadata metadata,
            @JsonProperty(value = "criteria", required = true) final ExecutionResourceCriteria criteria,
            @JsonProperty(
                value = "requestedAgentConfig",
                required = true
            ) final AgentConfigRequest requestedAgentConfig
    ) {
            super();
            this.bMetadata = metadata;
            this.bCriteria = criteria;
            this.bRequestedAgentConfig = requestedAgentConfig;
        }

        /**
         * Set the ordered list of command line arguments to append to the command executable at runtime.
         *
         * @param commandArgs The arguments in the order they should be placed on the command line. Maximum of 10,000
         *                    characters per argument. Blank strings are removed
         * @return The builder
         */
        public Builder withCommandArgs(@Nullable final List<String> commandArgs) {
            this.bCommandArgs.clear();
            if (commandArgs != null) {
                commandArgs.stream().filter(StringUtils::isNotBlank).forEach(this.bCommandArgs::add);
            }
            return this;
        }

        /**
         * Build an immutable job request instance.
         *
         * @return An immutable representation of the user supplied information for a job request
         */
        public AgentJobRequest build() {
            return new JobRequest(this);
        }
    }
}
