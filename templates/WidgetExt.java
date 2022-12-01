<#if process == 'ios'>@com.google.j2objc.annotations.WeakOuter</#if>		
	public class <@getWidgetClassNameShortName myclass=myclass></@getWidgetClassNameShortName>Ext extends <@getWidgetClassName myclass=myclass></@getWidgetClassName> implements ILifeCycleDecorator<#if !myclass.createDefault?contains("maxDimensionSupported|")>, com.ashera.widget.IMaxDimension</#if>{
		private MeasureEvent measureFinished = new MeasureEvent();
		private OnLayoutEvent onLayoutEvent = new OnLayoutEvent();
		<#if !myclass.createDefault?contains("maxDimensionSupported|")>
		private int mMaxWidth = -1;
		private int mMaxHeight = -1;
		@Override
		public void setMaxWidth(int width) {
			mMaxWidth = width;
		}
		@Override
		public void setMaxHeight(int height) {
			mMaxHeight = height;
		}
		@Override
		public int getMaxWidth() {
			return mMaxWidth;
		}
		@Override
		public int getMaxHeight() {
			return mMaxHeight;
		}
		</#if>

		<#if process == 'android' && !myclass.createDefault?contains("ExcludeStyle|")>
		public <@getWidgetClassNameShortName myclass=myclass></@getWidgetClassNameShortName>Ext(Context context, android.util.AttributeSet attrs, int defStyleAttr) {
	        super(context, attrs, defStyleAttr);
	    }

		<#if process == 'android'  && !myclass.createDefault?contains("ExcludeDefStyleRes|")>
	    public <@getWidgetClassNameShortName myclass=myclass></@getWidgetClassNameShortName>Ext(Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes) {
	    	super(context, attrs, defStyleAttr, defStyleRes);
	    }
	    </#if>
	    </#if>
		public <@getWidgetClassNameShortName myclass=myclass></@getWidgetClassNameShortName>Ext(<#if process == 'android'>Context context</#if>) {
			<#if process == 'android'>super(context);</#if>
			<#if (process == 'ios' && viewgroup)>super();</#if>
			<#if process == 'ios' && !viewgroup && !myclass.widgetClassname?starts_with('Measurable')>super();</#if>
			<#if process == 'ios' && !viewgroup && myclass.widgetClassname?starts_with('Measurable')>super(${myclass.widgetName}.this);</#if>
			<#if ((process == 'swt' || process == 'web') && viewgroup)>super();</#if>
			<#if (process == 'swt' || process == 'web') && !viewgroup && !myclass.widgetClassname?starts_with('Measurable')>super();</#if>
			<#if (process == 'swt' || process == 'web') && !viewgroup && myclass.widgetClassname?starts_with('Measurable')>super(${myclass.widgetName}.this);</#if>
		}
		<#if myclass.createDefault?contains("ConstructorMode|")>
			public <@getWidgetClassNameShortName myclass=myclass></@getWidgetClassNameShortName>Ext(<#if process == 'android'>Context context, int mode</#if>) {
				<#if process == 'android'>super(context, mode);</#if>
			}
		</#if>
		
		@Override
		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			<#if myclass.createDefault?contains("updateMeasureSpec|")>
			heightMeasureSpec = updateHeightMeasureSpec(heightMeasureSpec);
			widthMeasureSpec = updateWidthMeasureSpec(widthMeasureSpec);
			</#if>

			<#if !myclass.createDefault?contains("maxDimensionSupported|")>
			if(mMaxWidth > 0) {
	        	widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, MeasureSpec.AT_MOST);
	        }
	        if(mMaxHeight > 0) {
	            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);

	        }
	        </#if>

	        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			<#if myclass.createDefault?contains("measureChild|")>
			this.widthMeasureSpec = widthMeasureSpec;
    		this.heightMeasureSpec = heightMeasureSpec;
			</#if>
			IWidgetLifeCycleListener listener = (IWidgetLifeCycleListener) getListener();
			if (listener != null) {
			    measureFinished.setWidth(getMeasuredWidth());
			    measureFinished.setHeight(getMeasuredHeight());
				listener.eventOccurred(EventId.measureFinished, measureFinished);
			}
			<#if myclass.createDefault?contains("postOnMeasure|")>
			postOnMeasure(widthMeasureSpec, heightMeasureSpec);
			</#if>
		}
		
		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			super.onLayout(changed, l, t, r, b);
			<#if process == 'swt' || process == 'ios' || process == 'web'>
			ViewImpl.setDrawableBounds(${myclass.widgetName}.this, l, t, r, b);
			</#if>
			<#if !myclass.createDefault?contains("skipSetVisibility|")><#if myclass.widgetName == 'ScrollViewImpl' || myclass.createDefault?contains("computeVerticalScrollRange|")>
			ViewImpl.nativeMakeFrame(asNativeWidget(), l, t, r, b, (int) (computeVerticalScrollRange()));
			<#elseif myclass.widgetName == 'HorizontalScrollViewImpl'>
			ViewImpl.nativeMakeFrameForHorizontalScrollView(asNativeWidget(), l, t, r, b, (int) (computeHorizontalScrollRange()));
			<#else>			
			ViewImpl.nativeMakeFrame(asNativeWidget(), l, t, r, b);
			</#if></#if>
			<#if myclass.createDefault?contains("nativeMakeFrameForChildWidget|")>
			nativeMakeFrameForChildWidget(l, t, r, b);
			</#if>
			replayBufferedEvents();
			<#if myclass.createDefault?contains("createcanvas|")>
			canvas.reset();
			onDraw(canvas);
	        </#if>
	        <#if process == 'swt' || process == 'ios' || process == 'web'>
	        ViewImpl.redrawDrawables(${myclass.widgetName}.this);
			</#if>
			
			IWidgetLifeCycleListener listener = (IWidgetLifeCycleListener) getListener();
			if (listener != null) {
				onLayoutEvent.setB(b);
				onLayoutEvent.setL(l);
				onLayoutEvent.setR(r);
				onLayoutEvent.setT(t);
				onLayoutEvent.setChanged(changed);
				listener.eventOccurred(EventId.onLayout, onLayoutEvent);
			}
			
			if (isInvalidateOnFrameChange() && isInitialised()) {
				${myclass.widgetName}.this.invalidate();
			}
		}	
		
		<#if process == 'android'>
		@Override
		public void onDraw(Canvas canvas) {
			Runnable runnable = () -> super.onDraw(canvas);
			executeMethodListeners("onDraw", runnable, canvas);
		}

		@Override
		public void draw(Canvas canvas) {
			Runnable runnable = () -> super.draw(canvas);
			executeMethodListeners("draw", runnable, canvas);
		}

		@SuppressLint("WrongCall")
		@Override
		public void execute(String method, Object... args) {
			switch (method) {
				case "onDraw":
					setOnMethodCalled(true);
					super.onDraw((Canvas) args[0]);
					break;

				case "draw":
					setOnMethodCalled(true);
					super.draw((Canvas) args[0]);
					break;

				default:
					break;
			}
		}
		</#if>
		<#if (process == 'ios' || process == 'swt' || process == 'web')>		
		@Override
		public void execute(String method, Object... canvas) {
			
		}
		</#if>

		public void updateMeasuredDimension(int width, int height) {
			setMeasuredDimension(width, height);
		}


		@Override
		public ILifeCycleDecorator newInstance(IWidget widget) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setAttribute(WidgetAttribute widgetAttribute,
				String strValue, Object objValue) {
			throw new UnsupportedOperationException();
		}		
		

		@Override
		public List<String> getMethods() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void initialized() {
			throw new UnsupportedOperationException();
		}
		
        @Override
        public Object getAttribute(WidgetAttribute widgetAttribute) {
            throw new UnsupportedOperationException();
        }
        @Override
        public void drawableStateChanged() {
        	super.drawableStateChanged();
        	ViewImpl.drawableStateChanged(${myclass.widgetName}.this);
        }
        <#if process == 'swt' || process == 'web' || process == 'ios'>
		@Override
		public void offsetTopAndBottom(int offset) {
			super.offsetTopAndBottom(offset);
			ViewImpl.nativeMakeFrame(asNativeWidget(), getLeft(), getTop(), getRight(), getBottom());
		}
		@Override
		public void offsetLeftAndRight(int offset) {
			super.offsetLeftAndRight(offset);
			ViewImpl.nativeMakeFrame(asNativeWidget(), getLeft(), getTop(), getRight(), getBottom());
		}
        <#if !myclass.createDefault?contains("skipSetVisibility|")>
        @Override
        public void setVisibility(int visibility) {
            super.setVisibility(visibility);
            <#if process == 'swt'>
            ((org.eclipse.swt.widgets.Control)asNativeWidget()).setVisible(View.VISIBLE == visibility);
            </#if>
            <#if process == 'web'>
            ((HTMLElement)asNativeWidget()).getStyle().setProperty("display", visibility != View.VISIBLE ? "none" : "block");
            </#if>
            <#if process == 'ios'>
            ViewImpl.nativeSetVisibility(asNativeWidget(), visibility != View.VISIBLE);
            </#if>
            
        }
        </#if>
        </#if>
		<#if myclass.createDefault?contains("autosize|")>
	 	@Override
        public boolean suggestedSizeFitsInSpace(int mAutoSizeTextSizeInPx, r.android.graphics.RectF availableSpace) {
        	return ${myclass.widgetName}.this.suggestedSizeFitsInSpace(mAutoSizeTextSizeInPx, availableSpace.width(), availableSpace.height());
        }
        
        @Override
        protected void setTextSizeInternal(int unit, float optimalTextSize, boolean b) {
        	${myclass.widgetName}.this.setMyTextSize(optimalTextSize);
        }
		</#if>
		<#if myclass.createDefault?contains("lineheight|")>
		  public int getBorderPadding(){
		    return ${myclass.widgetName}.this.getBorderPadding();
		  }

		  public int getLineHeight(){
		    return ${myclass.widgetName}.this.getLineHeight();
		  }

		  public int getBorderWidth(){
		    return ${myclass.widgetName}.this.getBorderWidth();
		  }
		  public int getLineHeightPadding(){
		    return ${myclass.widgetName}.this.getLineHeightPadding();
		  }
		</#if>
		<#if myclass.createDefault?contains("onRtlPropertiesChanged|")>
		 @Override
		 public void onRtlPropertiesChanged(int layoutDirection) {
			 ${myclass.widgetName}.this.onRtlPropertiesChanged(layoutDirection);
		 }
		</#if>
		<#if myclass.createDefault?contains("nativeMeasureDimension|")>
        @Override
        public int nativeMeasureWidth(java.lang.Object uiView) {
        	return ${myclass.widgetName}.this.nativeMeasureWidth(uiView);
        }
        
        @Override
        public int nativeMeasureHeight(java.lang.Object uiView, int width) {
        	return ${myclass.widgetName}.this.nativeMeasureHeight(uiView, width);
        }
		</#if>
		<#if myclass.createDefault?contains("smoothSlideViewTo|")>
		 @Override
	        public void smoothSlideViewTo(r.android.view.View drawerView, int x, int y) {
			 ${myclass.widgetName}.this.smoothSlideViewTo(drawerView, x, y);
	        }
		</#if>
		<#if myclass.createDefault?contains("ActionMenuViewImpl|")>
        @Override
        public r.android.view.View getItemView(r.android.view.MenuItem item) {
        	return ActionMenuViewImpl.this.getItemView(item);
        }
        
        @Override
        public r.android.view.View getOverFlowButton() {
        	return ActionMenuViewImpl.this.getOverFlowButton();
        }
        </#if>
        <#if myclass.createDefault?contains("measureChild|")>
    	private int widthMeasureSpec;
    	private int heightMeasureSpec;
    	public void measureChild(r.android.view.View child, int noOfColumns) {
    		int widthMeasureSpec = this.widthMeasureSpec;
			if (noOfColumns != -1) {
				int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
			    int myWidth = (int) ((1f * parentWidth)  / noOfColumns );
			    widthMeasureSpec = MeasureSpec.makeMeasureSpec(myWidth, MeasureSpec.EXACTLY);
			}

    		super.measureChild(child, widthMeasureSpec, heightMeasureSpec);
    	}
        </#if>
	}