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

package com.gwtplatform.carstore.client.application.manufacturer;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.carstore.client.application.ApplicationPresenter;
import com.gwtplatform.carstore.client.application.event.ActionBarEvent;
import com.gwtplatform.carstore.client.application.event.ActionBarVisibilityEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent;
import com.gwtplatform.carstore.client.application.event.ChangeActionBarEvent.ActionType;
import com.gwtplatform.carstore.client.application.manufacturer.ManufacturerPresenter.MyProxy;
import com.gwtplatform.carstore.client.application.manufacturer.ManufacturerPresenter.MyView;
import com.gwtplatform.carstore.client.application.manufacturer.event.ManufacturerAddedEvent;
import com.gwtplatform.carstore.client.application.manufacturer.ui.EditManufacturerPresenter;
import com.gwtplatform.carstore.client.place.NameTokens;
import com.gwtplatform.carstore.client.rest.ManufacturerService;
import com.gwtplatform.carstore.client.util.AbstractAsyncCallback;
import com.gwtplatform.carstore.client.util.ErrorHandlerAsyncCallback;
import com.gwtplatform.carstore.shared.dto.ManufacturerDto;
import com.gwtplatform.dispatch.rest.shared.RestDispatch;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.ProxyEvent;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest.Builder;

public class ManufacturerPresenter extends Presenter<MyView, MyProxy>
        implements ManufacturerUiHandlers, ActionBarEvent.ActionBarHandler {

    interface MyView extends View, HasUiHandlers<ManufacturerUiHandlers> {
        void addManufacturer(ManufacturerDto manufacturerDto);

        void displayManufacturers(List<ManufacturerDto> manufacturerDtos);

        void removeManufacturer(ManufacturerDto manufacturerDto);

        void replaceManufacturer(ManufacturerDto oldManufacturer, ManufacturerDto newManufacturer);
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.MANUFACTURER)
    interface MyProxy extends ProxyPlace<ManufacturerPresenter> {
    }

    private final RestDispatch dispatcher;
    private final PlaceManager placeManager;
    private final ManufacturerService manufacturerService;
    private final EditManufacturerPresenter editManufacturerPresenter;

    private ManufacturerDto editingManufacturer;

    @Inject
    ManufacturerPresenter(EventBus eventBus,
                          MyView view,
                          MyProxy proxy,
                          RestDispatch dispatcher,
                          ManufacturerService manufacturerService,
                          PlaceManager placeManager,
                          EditManufacturerPresenter editManufacturerPresenter) {
        super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN_CONTENT);

        this.dispatcher = dispatcher;
        this.placeManager = placeManager;
        this.manufacturerService = manufacturerService;
        this.editManufacturerPresenter = editManufacturerPresenter;

        getView().setUiHandlers(this);
    }

    @Override
    public void onActionEvent(ActionBarEvent event) {
        if (event.getActionType() == ActionType.ADD && event.isTheSameToken(NameTokens.getManufacturer())) {
            placeManager.revealPlace(new Builder().nameToken(NameTokens.getDetailManufacturer()).build());
        }
    }

    @Override
    public void onDetail(ManufacturerDto manufacturerDto) {
        PlaceRequest placeRequest = new Builder().nameToken(NameTokens.getDetailManufacturer())
                                                 .with("id", String.valueOf(manufacturerDto.getId()))
                                                 .build();

        placeManager.revealPlace(placeRequest);
    }

    @Override
    public void onEdit(ManufacturerDto manufacturerDto) {
        editingManufacturer = manufacturerDto;
        editManufacturerPresenter.edit(manufacturerDto);
    }

    @Override
    public void onCreate() {
        editingManufacturer = null;
        editManufacturerPresenter.createNew();
    }

    @Override
    public void onDelete(final ManufacturerDto manufacturerDto) {
        dispatcher.execute(manufacturerService.delete(manufacturerDto.getId()),
                new ErrorHandlerAsyncCallback<Void>(this) {
                    @Override
                    public void onSuccess(Void nothing) {
                        getView().removeManufacturer(manufacturerDto);
                    }
                });
    }

    @Override
    protected void onReveal() {
        ActionBarVisibilityEvent.fire(this, true);
        ChangeActionBarEvent.fire(this, Arrays.asList(ActionType.ADD), true);

        dispatcher.execute(manufacturerService.getManufacturers(), new AbstractAsyncCallback<List<ManufacturerDto>>() {
            @Override
            public void onSuccess(List<ManufacturerDto> manufacturers) {
                getView().displayManufacturers(manufacturers);
            }
        });
    }

    @Override
    protected void onBind() {
        addRegisteredHandler(ActionBarEvent.getType(), this);
    }

    @ProxyEvent
    void onManufacturerAdded(ManufacturerAddedEvent event) {
        if (editingManufacturer != null) {
            getView().replaceManufacturer(editingManufacturer, event.getManufacturer());
        } else {
            getView().addManufacturer(event.getManufacturer());
        }

        editingManufacturer = event.getManufacturer();
    }
}
