package gov.nasa.jpl.clipper.uvs.calibration;


import gov.nasa.jpl.aerie.merlinsk.ActivityDefinition;
import gov.nasa.jpl.aerie.merlinsk.ActivityParameterDefinition;
import gov.nasa.jpl.aerie.merlinsk.BasicParameters;
import gov.nasa.jpl.clipper.uvs.Ports;

/**
 * static factory class used to construct bright star stare activity definition
 */
public class Observation_v1_4_1 {
  /**
   * construct an activity definition for a bright star raster uvs calibration
   *
   * @return activity definition for bright star raster
   */
  static ActivityDefinition build() {
    new actDef = new ActivityDefinition();

    /*metadata*/
    General.applyMetadata( actDef );
    actDef.setName( "UvsCalibration" );
    actDef.setVersion( "1.4.1" );
    actDef.setSubsystem( "UVS" );
    actDef.setBrief( "collect calibration data from the uvs" );
    actDef.setDocumentation(
      "opens the specified port on the uvs instrument and begins collecting "
      +" photon data for calibration purposes. note that the target pointing "
      +" must be aquired and maintained seprately from this activity, typically "
      +" by a parent calibration type."
      );
    actDef.addReferences( 
      "https://github.jpl.nasa.gov/Europa/OPS/blob/clipper-develop/seq/aaf/Clipper/Instruments/UVS_activities.aaf#L755",
      );
    
    /*parameters*/
    { ActivityParameterDefinition pDef = BasicParameters.buildEnum( 
        uvs.Ports.class );
      pDef.setName( "port" );
      pDef.setBrief( "the instrument port to use to collect data" );
      pDef.setDocumentation(
        "specifies the port on the uvs instrument that should be opened and "
        +" used to collect photon data for the calbiration, selected from "
        +" the set of available uvs instrument ports"
        );
      actDef.addParameter( pDef );
    }

    { ActivityParameterDefinition pDef = BasicParameters.buildDuration();
      pDef.setBrief( "the calibration exposure duration" );
      pDef.setDocumentation( 
        "the duration over which the uvs should open its shutter and collect "
        +" calibration photon data"
        );
    }
     
    { ActivityParameterDefinition pDef = BasicParameters.buildDataVolume();
      pDef.setName( "dataVolume" );
      pDef.setBrief( "anticipated calibration data volume from observation" );
      pDef.setDocumentation( 
        "the maximum-liklihood expectation of the data volume generated by "
        +" this calibration observation"
        );
    }
    
    { ActivityParameterDefinition pDef = BasicParameters.buildEnum( 
        ObservationMode.class );
      pDef.setName( "observationMode" );
      pDef.setBrief( "the kind of calibration observation being conducted" );
      pDef.setDocumentation(
        "specifies the kind of observation being conducted, which influences "
        +" the power/operations mode that the instrument assumes during the "
        +" exposure (eg dark calibrations differences). selected from the "
        +" available observation types"
        );
      actDef.addParameter( pDef );
    }
    
    return actDef;
  }
  
}