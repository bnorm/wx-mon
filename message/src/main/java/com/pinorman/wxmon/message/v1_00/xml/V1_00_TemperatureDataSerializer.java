package com.pinorman.wxmon.message.v1_00.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.pinorman.wxmon.message.SerializeException;
import com.pinorman.wxmon.message.TemperatureSensorReading;
import com.pinorman.wxmon.message.TemperatureSensorReadingSerializer;

public class V1_00_TemperatureDataSerializer implements TemperatureSensorReadingSerializer {

    private static JAXBContext context;

    private JAXBContext getContext() throws SerializeException {
        try {
            if (context == null) {
                synchronized (this) {
                    if (context == null) {
                        context = JAXBContext.newInstance(TemperatureData.class.getPackage().getName());
                    }
                }
            }
        } catch (JAXBException e) {
            throw new SerializeException("There was a problem creating the JAXB context for the parser.", e);
        }

        return context;
    }

    @Override
    public List<TemperatureSensorReading> unmarshal(InputStream input) throws IOException, SerializeException {
        TemperatureData data;
        try {
            data = (TemperatureData) getContext().createUnmarshaller().unmarshal(input);
        } catch (JAXBException e) {
            throw new SerializeException("There was a problem unmarshaling the XML temperature data.", e);
        }

        List<TemperatureSensorReading> readings = new ArrayList<>(data.getReadings().size());
        for (TemperatureReading reading : data.getReadings()) {
            readings.add(new TemperatureSensorReading(reading.getValue(), reading.getTime()));
        }
        return readings;
    }

    @Override
    public void marshal(Iterable<? extends TemperatureSensorReading> data, Writer writer) throws SerializeException {
        TemperatureData temperatureData = new TemperatureData();
        for (TemperatureSensorReading reading : data) {
            TemperatureReading temperatureReading = new TemperatureReading();
            temperatureReading.setValue(reading.getValue());
            temperatureReading.setTime(reading.getTime());
            temperatureData.getReadings().add(temperatureReading);
        }

        try {
            getContext().createMarshaller().marshal(temperatureData, writer);
        } catch (JAXBException e) {
            throw new SerializeException("There was a problem marshaling the XML temperature data.", e);
        }
    }
}
