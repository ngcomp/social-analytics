package com.ngcomp.analytics.engine.web.hb;

import com.ngcomp.analytics.engine.domain.Sources;

import java.util.List;

public interface SourcesDAO {

	public List getSources();

    Sources getSource(Long id);

    List getSourceListForBrandId(Long brandId);
    
    void updateSource(Sources source);
}
