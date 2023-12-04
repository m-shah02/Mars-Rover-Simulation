import java.util.ArrayList;
import java.util.Iterator;

class Vehicle extends Entity {
	public boolean carryingSample;

	public Vehicle(Location l) {
		super(l);
		this.carryingSample = false;
	}

	public void act(Field f, Mothership m, ArrayList<Rock> rocksCollected) {
		actCollaborative(f, m, rocksCollected);
		// actSimple(f,m,rocksCollected);
	}

	public void moveTo(Field f, Location loc) {
		f.clearLocation(location);
		f.place(this, loc);
		this.setLocation(loc);
	}

	public void actCollaborative(Field f, Mothership m, ArrayList<Rock> rocksCollected) {

		// if carrying a sample and at the base then drop sample
		if (carryingSample && f.isNeighbourTo(this.location, Mothership.class)) {
			this.carryingSample = false;
			return;
		}

		// if carrying a sample and not at the base then drop two crumbs and travel up
		// gradient
		if (carryingSample) {
			f.dropCrumbs(this.location, 2);
			ArrayList<Location> freeLocations = f.getAllfreeAdjacentLocations(location);
			Location bestLocation = location;
			int highestSignal = f.getSignalStrength(location);
			Iterator<Location> iter = freeLocations.iterator();
			while (iter.hasNext()) {
				Location loc = iter.next();
				if (f.getSignalStrength(loc) > highestSignal) {
					bestLocation = loc;
					highestSignal = f.getSignalStrength(loc);
				}
			}
			moveTo(f, bestLocation);
			return;
		}

		// if detect a sample then pick sample
		if (f.isNeighbourTo(this.location, Rock.class)) {
			Location rockLoc = f.getNeighbour(this.location, Rock.class);
			this.carryingSample = true;
			Rock r = (Rock) f.getObjectAt(rockLoc);
			rocksCollected.add(r);
			f.clearLocation(rockLoc);
			return;
		}

		// if sense crumbs then pick up one crumb and travel down gradient
		if (f.getCrumbQuantityAt(this.location) > 0) {
			f.pickUpACrumb(this.location);
			ArrayList<Location> freeLocations = f.getAllfreeAdjacentLocations(location);
			Location bestLocation = location;
			int lowestSignal = f.getSignalStrength(location) - f.getCrumbQuantityAt(location);
			Iterator<Location> iter = freeLocations.iterator();
			while (iter.hasNext()) {
				Location loc = iter.next();
				// optimising travel down gradient to take crumbs into account (1c)
				int strength = f.getSignalStrength(loc) - f.getCrumbQuantityAt(loc);
				if (strength < lowestSignal) {
					bestLocation = loc;
					lowestSignal = strength;
				}
			}
			moveTo(f, bestLocation);
			return;
		}

		Location newLocation = f.freeAdjacentLocation(this.location);
		if (newLocation != null) {
			// if true then move randomly
			moveTo(f, newLocation);
			return;
		}
	}

	public void actSimple(Field f, Mothership m, ArrayList<Rock> rocksCollected) {

		// if carrying a sample and at the base then drop sample
		if (carryingSample && f.isNeighbourTo(this.location, Mothership.class)) {
			this.carryingSample = false;
			return;
		}

		// if carrying a sample and not at the base then travel up gradient
		if (carryingSample) {
			ArrayList<Location> freeLocations = f.getAllfreeAdjacentLocations(location);
			Location bestLocation = location;
			int highestSignal = f.getSignalStrength(location);
			Iterator<Location> iter = freeLocations.iterator();
			while (iter.hasNext()) {
				Location loc = iter.next();
				if (f.getSignalStrength(loc) > highestSignal) {
					bestLocation = loc;
					highestSignal = f.getSignalStrength(loc);
				}
			}
			moveTo(f, bestLocation);
			return;
		}

		// if detect a sample then pick sample
		if (f.isNeighbourTo(this.location, Rock.class)) {
			Location rockLoc = f.getNeighbour(this.location, Rock.class);
			this.carryingSample = true;
			Rock r = (Rock) f.getObjectAt(rockLoc);
			rocksCollected.add(r);
			f.clearLocation(rockLoc);
			return;
		}

		Location newLocation = f.freeAdjacentLocation(this.location);
		if (newLocation != null) {
			// if true then move randomly
			moveTo(f, newLocation);
			return;
		}
	}
}