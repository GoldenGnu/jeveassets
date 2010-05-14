package net.nikr.eve.jeveassets.tests.mocks;

import com.beimin.eveapi.eve.conquerablestationlist.ApiStation;
import java.awt.Dimension;
import java.awt.Point;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.AssetFilter;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.Jump;
import net.nikr.eve.jeveasset.data.Location;
import net.nikr.eve.jeveasset.data.PriceData;
import net.nikr.eve.jeveasset.data.PriceDataSettings;
import net.nikr.eve.jeveasset.data.Profile;
import net.nikr.eve.jeveasset.data.ReprocessSettings;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.UserItemName;
import net.nikr.eve.jeveasset.data.UserPrice;
import net.nikr.eve.jeveasset.io.online.PriceDataGetter;

/**
 *
 * @author Candle
 */
public abstract class FakeSettings extends Settings {

	public FakeSettings() {
		super(false);
	}

	@Override
	public void clearEveAssetList() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<Account> getAccounts() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public String getApiProxy() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, List<AssetFilter>> getAssetFilters() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<Long> getBpos() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Integer, ApiStation> getConquerableStations() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Date getConquerableStationsNextUpdate() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<EveAsset> getEventListAssets() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, Boolean> getFlags() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Integer, Item> getItems() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<Jump> getJumps() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Integer, Location> getLocations() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Integer, PriceData> getPriceData() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public PriceDataGetter getPriceDataGetter() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Date getPriceDataNextUpdate() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public PriceDataSettings getPriceDataSettings() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<Profile> getProfiles() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Proxy getProxy() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public ReprocessSettings getReprocessSettings() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<String> getTableColumnNames() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, String> getTableColumnTooltips() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<String> getTableColumnVisible() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<String> getTableNumberColumns() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<Integer> getUniqueIds() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Long, UserItemName> getUserItemNames() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<Integer, UserPrice> getUserPrices() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Point getWindowLocation() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Dimension getWindowSize() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean hasAssets() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isAutoResizeColumnsText() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isAutoResizeColumnsWindow() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isAutoUpdate() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isFilterOnEnter() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isForceUpdate() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isHighlightSelectedRows() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isReprocessColors() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isSettingsLoaded() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isUpdatable(Date date) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isUpdatable(Date date, boolean ignoreOnProxy) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isUpdateDev() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isWindowAutoSave() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isWindowMaximized() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void loadActiveProfile() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void saveAssets() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void saveSettings() {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setAccounts(List<Account> accounts) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setActiveProfile(Profile activeProfile) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setApiProxy(String apiProxy) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setAssetFilters(Map<String, List<AssetFilter>> assetFilters) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setAutoResizeColumnsText(boolean autoResizeColumns) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setAutoResizeColumnsWindow(boolean autoResizeColumns) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setAutoUpdate(boolean updateStable) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setConquerableStations(Map<Integer, ApiStation> conquerableStations) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setConquerableStationsNextUpdate(Date conquerableStationNextUpdate) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setFilterOnEnter(boolean filterOnEnter) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setHighlightSelectedRows(boolean filterOnEnter) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setItems(Map<Integer, Item> items) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setJumps(List<Jump> jumps) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setLocations(Map<Integer, Location> locations) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setPriceData(Map<Integer, PriceData> priceData) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setPriceDataSettings(PriceDataSettings priceDataSettings) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setProfiles(List<Profile> profiles) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setProxy(Proxy proxy) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setProxy(String host, int port, String type) throws IllegalArgumentException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setProxy(String host, int port, Type type) throws IllegalArgumentException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setReprocessColors(boolean updateDev) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setReprocessSettings(ReprocessSettings reprocessSettings) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setTableColumnNames(List<String> mainTableColumnNames) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setTableColumnVisible(List<String> mainTableColumnVisible) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setUpdateDev(boolean updateDev) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setUserItemNames(Map<Long, UserItemName> userItemNames) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setUserPrices(Map<Integer, UserPrice> userPrices) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setWindowAutoSave(boolean windowAutoSave) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setWindowLocation(Point windowLocation) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setWindowMaximized(boolean windowMaximized) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void setWindowSize(Dimension windowSize) {
		throw new UnsupportedOperationException("not implemented");
	}
}
