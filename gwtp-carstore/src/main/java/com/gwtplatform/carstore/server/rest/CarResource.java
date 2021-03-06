/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.carstore.server.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.gwtplatform.carstore.server.dao.CarDao;
import com.gwtplatform.carstore.server.dao.CarPropertiesDao;
import com.gwtplatform.carstore.server.dao.domain.Car;
import com.gwtplatform.carstore.shared.dto.CarDto;
import com.gwtplatform.carstore.shared.dto.CarPropertiesDto;
import com.gwtplatform.carstore.shared.rest.PathParameter;
import com.gwtplatform.carstore.shared.rest.ResourcesPath;
import com.gwtplatform.carstore.shared.rest.RestParameter;

@Path(ResourcesPath.CARS)
@Produces(MediaType.APPLICATION_JSON)
public class CarResource {
    private static final String DEFAULT_LIMIT = "1000";
    private static final String DEFAULT_OFFSET = "0";
    private static final Integer INT_DEFAULT_LIMIT = Integer.valueOf(DEFAULT_LIMIT);
    private static final Integer INT_DEFAULT_OFFSET = Integer.valueOf(DEFAULT_OFFSET);

    private final CarDao carDao;
    private final CarPropertiesDao carPropertiesDao;

    @Inject
    CarResource(CarDao carDao,
                CarPropertiesDao carPropertiesDao) {
        this.carDao = carDao;
        this.carPropertiesDao = carPropertiesDao;
    }

    @GET
    public Response getCars(@DefaultValue(DEFAULT_OFFSET) @QueryParam(RestParameter.OFFSET) int offset,
                            @DefaultValue(DEFAULT_LIMIT) @QueryParam(RestParameter.LIMIT) int limit) {
        List<CarDto> cars;

        if (offset == INT_DEFAULT_OFFSET && limit == INT_DEFAULT_LIMIT) {
            cars = Car.createDto(carDao.getAll());
        } else {
            cars = Car.createDto(carDao.getSome(offset, limit));
        }

        return Response.ok(cars).build();
    }

    @GET
    @Path(ResourcesPath.COUNT)
    public Response getCarsCount() {
        Integer carsCount = carDao.countAll();

        return Response.ok(carsCount).build();
    }

    @POST
    public Response saveOrCreate(CarDto carDto) {
        CarPropertiesDto carProperties = carDto.getCarProperties();
        if (carProperties != null && !carProperties.isSaved()) {
            carProperties = carPropertiesDao.put(carProperties);
            carDto.setCarProperties(carProperties);
        }

        Car car = carDao.put(Car.create(carDto));

        return Response.ok(Car.createDto(car)).build();
    }

    @Path(PathParameter.PATH_ID)
    @DELETE
    public Response delete(@PathParam(RestParameter.ID) Long id) {
        carDao.delete(id);

        return Response.ok().build();
    }
}
