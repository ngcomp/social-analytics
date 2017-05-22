package com.ngcomp.analytics.engine.connector.pinterest.model;

import com.google.gson.Gson;
import com.ngcomp.analytics.engine.connector.common.model.Data;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class PinterestResponse extends Data{
		private String type;
		private String version;
		private String provider_name;
		
		private String provider_url;
		private String title;
		private String author_name;
		private String author_url;
		private String thumbnail_url;
		private String url;
		private String description;
		private float price;
		private String currency_code;
		private int quantity;
		private String category;
		private String[] tags;

		
		public String getRowKey() {
			return url;
		}
		
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
		public String getProvider_name() {
			return provider_name;
		}
		public void setProvider_name(String provider_name) {
			this.provider_name = provider_name;
		}
		public String getProvider_url() {
			return provider_url;
		}
		public void setProvider_url(String provider_url) {
			this.provider_url = provider_url;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getAuthor_name() {
			return author_name;
		}
		public void setAuthor_name(String author_name) {
			this.author_name = author_name;
		}
		public String getAuthor_url() {
			return author_url;
		}
		public void setAuthor_url(String author_url) {
			this.author_url = author_url;
		}
		public String getThumbnail_url() {
			return thumbnail_url;
		}
		public void setThumbnail_url(String thumbnail_url) {
			this.thumbnail_url = thumbnail_url;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public float getPrice() {
			return price;
		}
		public void setPrice(float price) {
			this.price = price;
		}
		public String getCurrency_code() {
			return currency_code;
		}
		public void setCurrency_code(String currency_code) {
			this.currency_code = currency_code;
		}
		public int getQuantity() {
			return quantity;
		}
		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public String[] getTags() {
			return tags;
		}
		public void setTags(String[] tags) {
			this.tags = tags;
		}
		public String[] getMaterials() {
			return materials;
		}
		public void setMaterials(String[] materials) {
			this.materials = materials;
		}
		private String[] materials;
		
		public Map<String, String[]> getHBaseRowMap() {

			Gson gson = new Gson();

			String[] quals = new String[14];
			String[] vals = new String[14];

			quals[0] = "sourceId"; vals[0] = this.getSourceId();
			quals[1] = "marketId"; vals[1] = this.getMarketId();
			quals[2] = "brandId"; vals[2] = this.getBrandId();
			quals[3] = "owned"; vals[3] = String.valueOf(this.getOwned());
			quals[4] = "topic"; vals[4] = this.getTopic();
			quals[5] = "post"; vals[5] = gson.toJson(this, PinterestResponse.class);
			quals[6] = "originalTokens"; vals[6] = StringUtils.join(this.getOriginalTokens(), ",");
			quals[7] = "artificialTokens"; vals[7] = StringUtils.join(this.getArtificialTokens(), ",");
			quals[8 ] = "likeCount"   ; vals[8 ] = String.valueOf(this.getLikeCount());
	        quals[9 ] = "commentCount"; vals[9 ] = String.valueOf(this.getCommentCount());
	        quals[10] = "shareCount" ;  vals[10] = String.valueOf(this.getShareCount());
			quals[11] = "article"; vals[11] = this.description;
			quals[12] = "createdAt"; vals[12] = String.valueOf(this.url);
			quals[13] = "extraWeight"; vals[13] = String.valueOf(this.getExtraWeight());

			Map<String, String[]> map = new HashMap<String, String[]>();
			map.put("quals", quals);
			map.put("vals", vals);
			return map;
		}
}
