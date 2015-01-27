package com.xt.core.conv;

public class DefaultConverter<S, T> implements Converter<S, T> {
	
	private final BaseConverter<S> sourceConverter;
	
	private final BaseConverter<T> targetConverter;
	
	public DefaultConverter (BaseConverter<S> sourceConverter, BaseConverter<T> targetConverter) {
		this.sourceConverter = sourceConverter;
		this.targetConverter = targetConverter;
	}

	public T convert(Class<S> sourceClass, Class<T> targetClass, S value) {
		if (value == null) {
			return null;
		}
		
		String str = sourceConverter.convertToString(value);
		return targetConverter.convert(str);
	}

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultConverter<S, T> other = (DefaultConverter<S, T>) obj;
        if (this.sourceConverter != other.sourceConverter && (this.sourceConverter == null || !this.sourceConverter.equals(other.sourceConverter))) {
            return false;
        }
        if (this.targetConverter != other.targetConverter && (this.targetConverter == null || !this.targetConverter.equals(other.targetConverter))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.sourceConverter != null ? this.sourceConverter.hashCode() : 0);
        hash = 29 * hash + (this.targetConverter != null ? this.targetConverter.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder strBld = new StringBuilder();
        strBld.append("DefaultConverter [sourceConverter=").append(sourceConverter).append(";");
        strBld.append("targetConverter=").append(targetConverter).append("]");
        return strBld.toString();
    }

    
	

}
