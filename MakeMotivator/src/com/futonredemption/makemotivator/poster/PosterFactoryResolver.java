package com.futonredemption.makemotivator.poster;

import java.util.ArrayList;

import com.futonredemption.makemotivator.poster.fancy.FancyPosterElementFactory;
import com.futonredemption.makemotivator.poster.plain.PlainPosterElementFactory;

public class PosterFactoryResolver {
	public final ArrayList<IPosterElementFactory> factories = new ArrayList<IPosterElementFactory>();
	
	public PosterFactoryResolver() {
		
		factories.add(new FancyPosterElementFactory());
		factories.add(new PlainPosterElementFactory());
	}
	
	public IPosterElementFactory getFactory(String themeTag) {
		
		for(IPosterElementFactory factory : factories) {
			if(themeTag.equalsIgnoreCase(factory.getThemeTag())) {
				return factory;
			}
		}
		
		// Should never happen, default to first factory.
		return factories.get(0);
	}
}
